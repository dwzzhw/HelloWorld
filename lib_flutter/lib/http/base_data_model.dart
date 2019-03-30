import 'dart:convert';

import 'package:lib_flutter/http/http_controller.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/utils/cache_manager.dart';

abstract class BaseDataModel<T> extends Object {
  static const String TAG = "BaseDataModel";
  static const int ERROR_CODE_EXCEPTION = -1001;
  static const int ERROR_CODE_EMPTY_BODY = -1002;

  static const int RESP_TYPE_CACHE = 1;
  static const int RESP_TYPE_NET = 2;

  static const int REQ_TYPE_CACHE_OR_NET = 1;
  static const int REQ_TYPE_CACHE_AND_NET = 2;
  static const int REQ_TYPE_ONLY_NET = 3;

  OnDataCompleteFunc<T> onCompleteFunction;
  OnDataErrorFunc onErrorFunction;
  T mRespData;

  BaseDataModel(this.onCompleteFunction, {this.onErrorFunction});

  String getUrl();

  Map<String, String> getReqParamMap() => null;

  String getLogTAG() {
    return 'BaseDataModel';
  }

  String getCacheKey();

  bool needCache() {
    return false;
  }

  void loadData({int reqType = REQ_TYPE_CACHE_OR_NET}) {
    log('-->loadData(), reqType=$reqType');
    if (needCache() && reqType < REQ_TYPE_ONLY_NET) {
      log('-->try to get cache data first');

      new Future(() async {
        dynamic cachedPayload = await getCachedData();
        if (cachedPayload != null) {
          parseRespBody(cachedPayload, RESP_TYPE_CACHE);
        }
      }).whenComplete(() {
        if (mRespData != null) {
          _notifyDataComplete(RESP_TYPE_CACHE);
          if (reqType <= REQ_TYPE_CACHE_OR_NET) {
            return;
          }
        }
        loadDataFromNet();
      });
    } else {
      loadDataFromNet();
    }
  }

  void loadDataFromNet() {
    log('-->loadDataFromNet()');
    HttpController.get(getUrl(), onGetHttpRespBody, params: getReqParamMap(),
        errorCallback: (exception) {
      notifyDataError(ERROR_CODE_EXCEPTION, exception.toString());
    });
  }

  Future<dynamic> getCachedData() async {
    String cacheKey = getCacheKey();
    CachedPayloadJsonObj cachedPayloadObj =
        await CacheManager.readFromCache(cacheKey);
    log('-->getCachedData(), cacheKey=$cacheKey, cachedPayloadObj=$cachedPayloadObj');
    return cachedPayloadObj?.payloadJson;
  }

  void onGetHttpRespBody(String respBodyStr) {
    log('-->onGetRespBody(), response body=$respBodyStr');
    if (respBodyStr == null) {
      notifyDataError(
          ERROR_CODE_EMPTY_BODY, 'Fail to get response from server');
    } else {
      new Future(() {
        parseRespBody(respBodyStr, RESP_TYPE_NET);
      });
    }
  }

  parseRespBody(dynamic respBody, int respType) {
    final parsedMap = (respBody is String) ? jsonDecode(respBody) : respBody;

    int code = parsedMap['code'] as int;
    String version = parsedMap['version'] as String;
    String errMsg = parsedMap['msg'] as String;
    dynamic dataContentObj = parsedMap['data'];

    if (code != 0) {
      notifyDataError(code, errMsg);
    } else {
      parseDataContentObj(dataContentObj);
      _notifyDataComplete(respType);
      if (needCache() && mRespData != null && respType == RESP_TYPE_NET) {
        CacheManager.writeToCache(getCacheKey(), respBody);
      }
    }
  }

  ///解析数据载荷
  ///dataObj可能为Map<String, dynamic>或List<dynamic>
  void parseDataContentObj(dynamic dataObj);

  void _notifyDataComplete(int respType) {
    log('-->notifyDataComplete(), url=${getUrl()}, OnCompleteFunction=$onCompleteFunction, respType=$respType');
    if (onCompleteFunction != null) {
      onCompleteFunction(mRespData);
    }
  }

  void notifyDataError(int errorCode, String errorMsg) {
    log('-->notifyDataError(), errorCode=$errorCode, errorMsg=$errorMsg, OnErrorFunction=$onErrorFunction');
    if (onErrorFunction != null) {
      onErrorFunction(errorCode, errorMsg);
    }
  }

  void log(String logMsg) {
    logd(getLogTAG(), logMsg);
  }
}
