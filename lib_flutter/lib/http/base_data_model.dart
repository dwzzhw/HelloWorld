import 'dart:convert';

import '../http/http_controller.dart';
import '../http/net_request_listener.dart';
import '../utils/Loger.dart';
import '../utils/cache_manager.dart';
import '../utils/date_util.dart';

abstract class BaseDataModel<T> extends Object {
  static const String TAG = "BaseDataModel";

  static const int ERROR_CODE_EXCEPTION = -1001;
  static const int ERROR_CODE_EMPTY_BODY = -1002;

  static const int LOAD_DATA_TYPE_CACHE = 0;
  static const int LOAD_DATA_TYPE_REFRESH = 1;
  static const int LOAD_DATA_TYPE_LOAD_MORE = 2;
  static const int LOAD_DATA_TYPE_LOAD_PREV = 3;
  static const int LOAD_DATA_TYPE_PERIOD = 4;

  static const int REQ_TYPE_CACHE_OR_NET = 1;
  static const int REQ_TYPE_CACHE_AND_NET = 2;
  static const int REQ_TYPE_ONLY_NET = 3;

  OnDataCompleteFunc<T> onCompleteFunction;
  OnDataErrorFunc onErrorFunction;
  T mRespData;

  BaseDataModel(this.onCompleteFunction, this.onErrorFunction);

  String getUrl();

  Map<String, String> getReqParamMap() => null;

  String getLogTAG() {
    return 'BaseDataModel';
  }

  String getCacheKey();

  bool needCache() {
    return true;
  }

  int getCacheValidTimeInMil() {
    return 2 * DateUtil.HOUR_IN_MILL_SECONDS;
//    return DateUtil.MINUTE_IN_MILL_SECONDS;
  }

  void loadData({int reqType = REQ_TYPE_CACHE_OR_NET}) {
    log('-->loadData(), reqType=$reqType');
    if (needCache() && reqType < REQ_TYPE_ONLY_NET) {
      log('-->try to get cache data first');

      new Future(() async {
        dynamic cachedPayload = await getCachedData();
        if (cachedPayload != null) {
          parseCachedPayload(cachedPayload);
        }
      }).whenComplete(() {
        if (mRespData != null) {
          _notifyDataComplete(LOAD_DATA_TYPE_CACHE);
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
      notifyDataError(
          ERROR_CODE_EXCEPTION, exception.toString(), LOAD_DATA_TYPE_REFRESH);
    });
  }

  void cancelNetReq() {
    //TODO to be implement
  }

  Future<dynamic> getCachedData() async {
    String cacheKey = getCacheKey();
    dynamic payload;
    CachedPayloadJsonObj cachedPayloadObj =
        await CacheManager.readFromCache(cacheKey);
    log('-->getCachedData(), cacheKey=$cacheKey, cachedPayloadObj=$cachedPayloadObj');

    if (cachedPayloadObj != null) {
      int cacheTime = int.tryParse(cachedPayloadObj.time);
      if (cacheTime != null &&
          DateTime.now().millisecondsSinceEpoch - cacheTime <
              getCacheValidTimeInMil()) {
        payload = cachedPayloadObj.payloadJson;
      } else {
        log('-->Cache for $cacheKey is expired');
      }
    }
    return payload;
  }

  void onGetHttpRespBody(String respBodyStr) {
    log('-->onGetRespBody(), response body=$respBodyStr');
    if (respBodyStr == null) {
      notifyDataError(ERROR_CODE_EMPTY_BODY, 'Fail to get response from server',
          LOAD_DATA_TYPE_REFRESH);
    } else {
      parseHttpRespBody(respBodyStr);
    }
  }

  ///解析网路请求返回中的body部分
  void parseHttpRespBody(String respBody) {
    final parsedMap = jsonDecode(respBody);

    int code = parsedMap['code'] as int;
    String version = parsedMap['version'] as String;
    String errMsg = parsedMap['msg'] as String;
    dynamic dataContentObj = parsedMap['data'];

    if (code != 0) {
      notifyDataError(code, errMsg, LOAD_DATA_TYPE_REFRESH);
    } else {
      parseDataContentObj(dataContentObj);
      _notifyDataComplete(LOAD_DATA_TYPE_REFRESH);
      if (needCache() && mRespData != null) {
        CacheManager.writeToCache(getCacheKey(), respBody);
      }
    }
  }

  ///解析从缓存中读取出来的内容，该内容已经通过json decode过
  void parseCachedPayload(dynamic cachePayload) {
    if (cachePayload is Map<String, dynamic>) {
      int code = cachePayload['code'] as int;
      dynamic dataContentObj = cachePayload['data'];

      if (code == 0 && dataContentObj != null) {
        parseDataContentObj(dataContentObj);
      }
    }
  }

  ///解析数据载荷
  ///dataObj可能为Map<String, dynamic>或List<dynamic>
  void parseDataContentObj(dynamic dataObj);

  void _notifyDataComplete(int respType) {
    log('-->notifyDataComplete(), url=${getUrl()}, respType=$respType, OnCompleteFunction=$onCompleteFunction');
    if (onCompleteFunction != null) {
      onCompleteFunction(this, respType);
    }
  }

  void notifyDataError(int errorCode, String errorMsg, int respType) {
    log('-->notifyDataError(), errorCode=$errorCode, errorMsg=$errorMsg, respType=$respType, OnErrorFunction=$onErrorFunction');
    if (onErrorFunction != null) {
      onErrorFunction(this, errorCode, errorMsg, respType);
    }
  }

  void log(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }
}
