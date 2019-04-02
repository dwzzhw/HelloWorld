import 'package:json_annotation/json_annotation.dart';

part 'news_item.g.dart';

@JsonSerializable()
class NewsItem extends Object {
  static const int ITEM_NORMAL = 0;
  static const int ITEM_MULTI_IMG = 1; // 组图类型
  static const int ITEM_VIDEO = 2; //视频文章类型（atype=2或3）新增vid字段
  static const int ITEM_VIDEO_ONLY = 3;
  static const int ITEM_WEBVIEW = 6; // 链接型文章
  static const int ITEM_SPECIAL = 11; //专题
  static const int ITEM_VIDEO_SPECIAL = 23; //视频专辑

  String getNewTypeDescStr() {
    String descStr;
    switch (int.tryParse(atype)) {
      case ITEM_MULTI_IMG:
        descStr = '图集';
        break;
      case ITEM_SPECIAL:
        descStr = '专题';
        break;
      case ITEM_VIDEO_SPECIAL:
        descStr = '专辑';
        break;
      case ITEM_WEBVIEW:
        descStr = 'H5';
        break;
    }
    return descStr;
  }

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
