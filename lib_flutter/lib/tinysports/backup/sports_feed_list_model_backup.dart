import 'dart:convert';

import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/http/http_controller.dart';

String TAG = 'SportsFeedListModel';

class SportsFeedListModel {
  void fetchFeedList(String idList, Function callback) {
    Map<String, String> params = Map();
    params['ids'] = idList;
    HttpController.post('http://preapp.sports.qq.com/feed/list', (respBody) {
      parseFeedListJson(callback, respBody);
    }, params: params);
//    return compute(parseFeedListJson, response.body);
  }

  List<FeedItemDetailInfo> parseFeedListJson(
      Function callback, String jsonStr) {
//  logd(TAG, '-->parseFeedListJson()');
    final parsedMap = jsonDecode(jsonStr);
    FeedList feedListPO = FeedList.fromJson(parsedMap);
    List<FeedItemDetailInfo> result = feedListPO?.getFeedDetailInfoList();
    logd('FeedListModel', '-->parseFeedListJson() done, result=$result');
    if (callback != null) {
      callback(result);
    }
    return result;
  }
}
