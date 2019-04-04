import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/data/video_item.dart';

part 'match_detail_related_info.g.dart';

@JsonSerializable()
class MatchDetailRelatedInfo extends Object {
  RelatedVideoList afterVideos;

  RelatedVideoList afterRecord;

  RelatedNews relatedNews;

  MatchDetailRelatedInfo(
    this.afterVideos,
    this.afterRecord,
    this.relatedNews,
  );

  factory MatchDetailRelatedInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$MatchDetailRelatedInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$MatchDetailRelatedInfoToJson(this);
}

@JsonSerializable()
class RelatedVideoList extends Object {
  String text;

  String vids;

  List<VideoItem> list;

  RelatedVideoList(
    this.text,
    this.vids,
    this.list,
  );

  factory RelatedVideoList.fromJson(Map<String, dynamic> srcJson) =>
      _$RelatedVideoListFromJson(srcJson);

  Map<String, dynamic> toJson() => _$RelatedVideoListToJson(this);
}

@JsonSerializable()
class RelatedNews extends Object {
  String text;

  String hasMore;

  List<NewsItem> items;

  String topicPosition;

  RelatedNews(
    this.text,
    this.hasMore,
    this.items,
    this.topicPosition,
  );

  factory RelatedNews.fromJson(Map<String, dynamic> srcJson) =>
      _$RelatedNewsFromJson(srcJson);

  Map<String, dynamic> toJson() => _$RelatedNewsToJson(this);
}
