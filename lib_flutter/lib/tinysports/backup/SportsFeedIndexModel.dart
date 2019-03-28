import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';
import 'package:lib_flutter/utils/Loger.dart';

String TAG = 'SportsFeedIndexModel';

class SportsFeedIndexModel {
  Future<List<FeedIndexItem>> fetchFeedIndexList() async {
    final response =
        await http.Client().get('http://preapp.sports.qq.com/feed/index');
    logd(TAG, '-->fetchFeedIndexList(), response=, body=${response.body}');
    return compute(parseFeedIndexJson, response.body);
  }
}

List<FeedIndexItem> parseFeedIndexJson(String jsonStr) {
  logd(TAG, '-->parseFeedIndexJson()');
  final parsedMap = jsonDecode(jsonStr);
  FeedIndex indexPO = FeedIndex.fromJson(parsedMap);
  return indexPO?.getFeedIndexList();
}
