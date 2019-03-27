import 'dart:convert';

import 'package:lib_flutter/http/http_controller.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/utils/Loger.dart';

abstract class BaseDataModel<T> extends Object {
  static const int ERROR_CODE_EXCEPTION = -1001;
  static const int ERROR_CODE_EMPTY_BODY = -1002;
  static final String TAG = "BaseDataModel";
  NetRequestListener mListener;
  Function mCompleteFunction;
  T mRespData;

  BaseDataModel(this.mCompleteFunction);

  //  BaseDataModel(this.mListener, {test(String)});

  String getUrl();

  Map<String, String> getReqParamMap() => null;

  void loadData() {
    HttpController.get(getUrl(), _onGetRespBody, params: getReqParamMap(),
        errorCallback: (exception) {
      notifyDataError(ERROR_CODE_EXCEPTION, exception.toString());
    });
  }

  void _onGetRespBody(String respBodyStr) {
    logd(TAG, '-->_onGetRespBody(), response=, body=$respBodyStr');
    if (respBodyStr == null) {
      notifyDataError(
          ERROR_CODE_EMPTY_BODY, 'Fail to get response from server');
    } else {
      new Future(() {
        parseRespBody(respBodyStr);
      });
    }
  }

  void parseRespBody(String respBody) {
    final parsedMap = jsonDecode(respBody);

    int code = parsedMap['code'] as int;
    String version = parsedMap['version'] as String;
    String errMsg = parsedMap['msg'] as String;
    dynamic dataContentObj = parsedMap['data'];

    if (code != 0) {
      notifyDataError(code, errMsg);
    } else {
      parseDataContentObj(dataContentObj);
      notifyDataComplete();
    }
  }

  ///解析数据载荷
  ///dataObj可能为Map<String, dynamic>或List<dynamic>
  void parseDataContentObj(dynamic dataObj);

  void notifyDataComplete() {
    if (mCompleteFunction != null) {
      mCompleteFunction();
    } else {
      mListener?.onDataComplete();
    }
  }

  void notifyDataError(int errorCode, String errorMsg) {
    mListener?.onDataError(errorCode, errorMsg);
  }
}
