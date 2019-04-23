import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/news_item.dart';
import '../../../tinysports/news/data/news_detail_item_content.dart';

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

  bool hasValidData() {
    return info != null &&
        info.sectionData != null &&
        info.sectionData.length > 0;
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
  List<NewsItem> artlist;

  String title;

  NewsSubjectSectionData(
    this.artlist,
    this.title,
  );

  factory NewsSubjectSectionData.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsSubjectSectionDataFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsSubjectSectionDataToJson(this);
}

class NewsSpecialListViewDataContainer {
  static const int VIEW_TYPE_PAGE_TITLE = 1;
  static const int VIEW_TYPE_PAGE_SUBTITLE = 2;
  static const int VIEW_TYPE_PAGE_PIC_HEADER = 3;
  static const int VIEW_TYPE_PAGE_GROUP_TITLE = 4;
  static const int VIEW_TYPE_PAGE_NORMAL_ITEM = 5;

  int viewType;
  dynamic data;

  NewsSpecialListViewDataContainer(this.viewType, this.data);
}
