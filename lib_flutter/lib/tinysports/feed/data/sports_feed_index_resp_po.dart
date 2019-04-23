import 'dart:convert';

import '../../../utils/Loger.dart';

class SportsFeedIndexRespPO {
  int code;
  String version;
  SportsFeedIndexInfo data;

  SportsFeedIndexRespPO(this.code, this.version, this.data);

  factory SportsFeedIndexRespPO.fromJson(String jsonStr) {
    final parsedMap = jsonDecode(jsonStr);
    llogd('FeedIndexPO', '-->SportsFeedIndexRespPO.fromJson(), parsedMap OK');
    return SportsFeedIndexRespPO(parsedMap['code'], parsedMap['version'],
        SportsFeedIndexInfo.fromJson(parsedMap['data']));
  }

  List<SportsFeedIndexItem> getFeedIndexList() {
    llogd('FeedIndexPO', '-->getFeedIndexList, data=$data');
    return data?.list;
  }
}

class SportsFeedIndexInfo {
  List<SportsFeedIndexItem> list;

  SportsFeedIndexInfo(this.list);

  factory SportsFeedIndexInfo.fromJson(Map<String, dynamic> jsonStr) {
    List itemJsonStrList = jsonStr['list'];
//    logd('FeedIndexPO',
//        '-->SportsFeedIndexInfo.fromJson(), list=$itemJsonStrList');
    llogd('FeedIndexPO', '-->SportsFeedIndexInfo.fromJson(), OK');

    List<SportsFeedIndexItem> tList = itemJsonStrList
        .map<SportsFeedIndexItem>((json) => SportsFeedIndexItem.fromJson(json))
        .toList();
//
//    logd('FeedIndexPO', '-->SportsFeedIndexInfo.fromJson(), tList=$tList');
    return SportsFeedIndexInfo(tList);
  }
}

class SportsFeedIndexItem {
  String id;
  String columnId;

  SportsFeedIndexItem(this.id, this.columnId);

  factory SportsFeedIndexItem.fromJson(Map<String, dynamic> jsonStrMap) {
//    logd('SportsFeedIndexItem', '-->fromJson, jsonStr id=${jsonStrMap["id"]}');
    return SportsFeedIndexItem(jsonStrMap['id'], jsonStrMap['columnId']);
  }
}
