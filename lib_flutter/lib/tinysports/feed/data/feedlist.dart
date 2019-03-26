import 'package:json_annotation/json_annotation.dart';
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

  List<FeedItemDetailInfo> getFeedDetailInfoList() {
//    logd('FeedIndexPO', '-->getFeedList, data=$data');

    List<FeedItemDetailInfo> detailList = List();
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
  @JsonKey(name: 'type')
  String type;

  @JsonKey(name: 'feedId')
  String feedId;

  @JsonKey(name: 'info')
  FeedItemDetailInfo info;

  FeedItemContent(
    this.type,
    this.feedId,
    this.info,
  );

  factory FeedItemContent.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedItemContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedItemContentToJson(this);
}

@JsonSerializable()
class FeedItemDetailInfo extends Object {
  @JsonKey(name: 'newsId')
  String newsId;

  @JsonKey(name: 'title')
  String title;

  @JsonKey(name: 'abstract')
  String abstract;

  @JsonKey(name: 'url')
  String url;

  @JsonKey(name: 'imgurl')
  String imgurl;

  @JsonKey(name: 'imgurl1')
  String imgurl1;

  @JsonKey(name: 'imgurl2')
  String imgurl2;

  @JsonKey(name: 'pub_time')
  String pubTime;

  @JsonKey(name: 'publishTime')
  String publishTime;

  @JsonKey(name: 'atype')
  String atype;

  @JsonKey(name: 'newsAppId')
  String newsAppId;

  @JsonKey(name: 'source')
  String source;

  @JsonKey(name: 'shareUrl')
  String shareUrl;

  FeedItemDetailInfo(
    this.newsId,
    this.title,
    this.abstract,
    this.url,
    this.imgurl,
    this.imgurl1,
    this.imgurl2,
    this.pubTime,
    this.publishTime,
    this.atype,
    this.newsAppId,
    this.source,
    this.shareUrl,
  );

  factory FeedItemDetailInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedItemDetailInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedItemDetailInfoToJson(this);
}
