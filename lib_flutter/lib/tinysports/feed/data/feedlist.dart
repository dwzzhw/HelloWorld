import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/utils/Loger.dart';

part 'feedlist.g.dart';

@JsonSerializable()
class FeedList extends Object {
  int code;
  Map<String, FeedItemContent> data;
  String version;

  FeedList(
    this.code,
    this.data,
    this.version,
  );

  factory FeedList.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedListFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedListToJson(this);

  List<NewsItem> getFeedDetailInfoList() {
    List<NewsItem> detailList = List();
    data?.forEach((key, content) {
      if (content != null && content.info != null) {
//        logd('FeedIndexPO', '-->loop detail item, key=$key');
        detailList.add(content.info);
      }
    });
    logd('FeedIndexPO',
        '-->getFeedDetailInfoList done, list length=${detailList.length}');
    return detailList;
  }
}

@JsonSerializable()
class FeedItemContent extends Object {
  String type;

  String feedId;

  NewsItem info;

  FeedItemContent(
    this.type,
    this.feedId,
    this.info,
  );

  factory FeedItemContent.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedItemContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedItemContentToJson(this);
}