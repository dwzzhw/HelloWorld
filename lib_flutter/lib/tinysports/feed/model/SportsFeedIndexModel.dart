import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:lib_flutter/tinysports/feed/data/SportsFeedIndexRespPO.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'dart:async';

String TAG = 'SportsFeedIndexModel';

List<SportsFeedIndexItem> parseFeedIndexJson(String jsonStr) {
  logd(TAG, '-->parseFeedIndexJson(), jsonStr 3');
  SportsFeedIndexRespPO indexPO = SportsFeedIndexRespPO.fromJson(jsonStr);
  logd(TAG, '-->parseFeedIndexJson()2, indexPO=$indexPO');
  return indexPO?.getFeedIndexList();
}

class SportsFeedIndexModel {
  Future<List<SportsFeedIndexItem>> fetchFeedIndexList() async {
    final response =
        await http.Client().get('http://preapp.sports.qq.com/feed/index');
    logd(TAG, '-->fetchFeedIndexList(), response=, body=${response.body}');
    return compute(parseFeedIndexJson, response.body);
  }
}
