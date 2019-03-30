import 'dart:convert';
import 'dart:io';

import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/utils/file_handler.dart';

class CacheManager {
  static const String TAG = 'CacheManager';
  static const String CACHE_DIR = 'cache';

  static void writeToCache(String cacheKey, dynamic payload) async {
    logd(TAG, '-->writeToCache(), cacheKey=$cacheKey, payload=$payload');
    if (cacheKey != null && cacheKey.length > 0 && payload != null) {
      File cacheFile = await FileHandler().getCacheFile('$CACHE_DIR/$cacheKey');
      cacheFile.create(recursive: true);
      CachedPayloadJsonObj cacheObj =
          CachedPayloadJsonObj.createNewInstance(payload);
      cacheFile.writeAsString(cacheObj.toString());
    }
  }

  static Future<CachedPayloadJsonObj> readFromCache(String cacheKey) async {
    CachedPayloadJsonObj cacheObj;
    try {
      if (cacheKey != null && cacheKey.length > 0) {
        File cacheFile =
            await FileHandler().getCacheFile('$CACHE_DIR/$cacheKey');
        if (await cacheFile.exists()) {
          String cachedStr = await cacheFile.readAsString();
//          logd(TAG,
//              '-->readFromCache(), cacheKey=$cacheKey, cachedStr=$cachedStr');
          if (cachedStr != null && cachedStr.length > 0) {
            dynamic parsedJson = jsonDecode(cachedStr);
//            logd(TAG, '-->readFromCache(), parsedJson=$parsedJson');
            cacheObj = CachedPayloadJsonObj.fromJson(parsedJson);
          }
        }
//        else {
//          logd(TAG,
//              '-->readFromCache(), cacheKey=$cacheKey, cache file not exit');
//        }
      }
    } catch (e, s) {
      logd(TAG, '-->Exception happens when parse cached obj, stack=$s');
    }

    logd(TAG, '-->readFromCache(), cacheKey=$cacheKey, cacheObj=$cacheObj');
    return cacheObj;
  }
}

class CachedPayloadJsonObj {
  String time;
  dynamic payloadJson;

  CachedPayloadJsonObj(this.payloadJson, this.time);

  CachedPayloadJsonObj.createNewInstance(dynamic payloadJson) {
    this.payloadJson = payloadJson;
    this.time = DateTime.now().millisecondsSinceEpoch.toString();
  }

  factory CachedPayloadJsonObj.fromJson(Map<String, dynamic> json) {
    return CachedPayloadJsonObj(json['payloadJson'], json['time'] as String);
  }

  Map<String, dynamic> toJson() =>
      <String, dynamic>{'time': time, 'payloadJson': payloadJson};

  @override
  String toString() {
    return '{"time": "$time", "payloadJson": $payloadJson}';
  }
}
