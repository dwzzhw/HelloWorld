import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';

part 'news_detail_item_subject_content.g.dart';

@JsonSerializable()
class NewsDetailItemSubjectContent extends NewsDetailItemContentBase {
  String type;
  NewsDetailItemSubjectInfo info;

  NewsDetailItemSubjectContent(
    this.info,
  );

  factory NewsDetailItemSubjectContent.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemSubjectContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemSubjectContentToJson(this);

  @override
  String getTypeStr() {
    return type;
  }
}

@JsonSerializable()
class NewsDetailItemSubjectInfo extends Object {
  String topType;

  String topPic;

  String pad_samll;

  String pad_big;

  NewsSubjectPicInfo newPic;

  String adTopPic_s;

  String adTopPic_b;

  String topUrl;

  String topTitle;

  String topId;

  String topFlag;

  String topFid;

  String topSurl;

  String topArttype;

  String topCataid;

  String topRevelationid;

  List<NewsSubjectSectionData> sectionData;

  NewsDetailItemSubjectInfo(
    this.topType,
    this.topPic,
    this.pad_samll,
    this.pad_big,
    this.newPic,
    this.adTopPic_s,
    this.adTopPic_b,
    this.topUrl,
    this.topTitle,
    this.topId,
    this.topFlag,
    this.topFid,
    this.topSurl,
    this.topArttype,
    this.topCataid,
    this.topRevelationid,
    this.sectionData,
  );

  factory NewsDetailItemSubjectInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemSubjectInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemSubjectInfoToJson(this);
}

@JsonSerializable()
class NewsSubjectPicInfo extends Object {
  String topPic;

  String pad_samll;

  String pad_big;

  String topPicW;

  String topPicH;

  String padSmallW;

  String padSmallH;

  String padBigW;

  String padBigH;

  NewsSubjectPicInfo(
    this.topPic,
    this.pad_samll,
    this.pad_big,
    this.topPicW,
    this.topPicH,
    this.padSmallW,
    this.padSmallH,
    this.padBigW,
    this.padBigH,
  );

  factory NewsSubjectPicInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsSubjectPicInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsSubjectPicInfoToJson(this);
}

@JsonSerializable()
class NewsSubjectSectionData extends Object {
  List<NewsSubjectArtlist> artlist;

  String title;

  NewsSubjectSectionData(
    this.artlist,
    this.title,
  );

  factory NewsSubjectSectionData.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsSubjectSectionDataFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsSubjectSectionDataToJson(this);
}

@JsonSerializable()
class NewsSubjectArtlist extends Object {
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

  String duration;

  String hasCopyRight;

  String vid;

  String commentsNum;

  String commentId;

  String tag_key;

  String user_tag_id;

  NewsSubjectArtlist(
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
    this.duration,
    this.hasCopyRight,
    this.vid,
    this.commentsNum,
    this.commentId,
    this.tag_key,
    this.user_tag_id,
  );

  factory NewsSubjectArtlist.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsSubjectArtlistFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsSubjectArtlistToJson(this);
}
