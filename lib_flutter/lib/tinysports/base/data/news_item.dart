import 'package:json_annotation/json_annotation.dart';

part 'news_item.g.dart';

@JsonSerializable()
class NewsItem extends Object {
  String newsId;

  String title;

  String abstract;

  String url;

  String imgurl;

  String imgurl1;

  String imgurl2;

  String pub_time;

  String publishTime;

  String atype;

  String newsAppId;

  String source;

  String shareUrl;

  String commentsNum;

  String commentId;

  NewsItem(
    this.newsId,
    this.title,
    this.abstract,
    this.url,
    this.imgurl,
    this.imgurl1,
    this.imgurl2,
    this.pub_time,
    this.publishTime,
    this.atype,
    this.newsAppId,
    this.source,
    this.shareUrl,
    this.commentsNum,
    this.commentId,
  );

  factory NewsItem.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsItemFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsItemToJson(this);
}
