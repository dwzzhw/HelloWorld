import 'package:http/http.dart' as http;
import 'package:lib_flutter/utils/Loger.dart';

class HttpController {
  static String TAG = 'HttpController';

  static void get(String url, Function callback,
      {Map<String, String> params, Function errorCallback}) async {
    logd(TAG, '-->get(), url=$url');
    if (params != null && params.isNotEmpty) {
      StringBuffer sb = new StringBuffer("?");
      params.forEach((key, value) {
        sb.write("$key" + "=" + "$value" + "&");
      });
      String paramStr = sb.toString();
      paramStr = paramStr.substring(0, paramStr.length - 1);
      url += paramStr;
    }
    try {
      http.Response res = await http.get(url);
      if (callback != null) {
        callback(res.body);
      }
    } catch (exception) {
      if (errorCallback != null) {
        errorCallback(exception);
      }
    }
  }

  static void post(String url, Function callback,
      {Map<String, String> params, Function errorCallback}) async {
    logd(TAG, '-->post(), url=$url');
    try {
      http.Response res = await http.post(url, body: params);

      if (callback != null) {
        callback(res.body);
      }
    } catch (e) {
      if (errorCallback != null) {
        errorCallback(e);
      }
    }
  }
}