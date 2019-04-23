import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/image_item.dart';
import '../../../tinysports/news/data/news_detail_item_subject_content.dart';

part 'news_detail_item_content.g.dart';

abstract class NewsDetailItemContentBase {
  static const int TYPE_NONE = -1;
  static const int TYPE_TEXT = 0;
  static const int TYPE_IMG = 1;
  static const int TYPE_VIDEO = 2;
  static const int TYPE_OTHER = 3;
  static const int TYPE_SPECIAL_SUBJECT = 4;
  static const int TYPE_LINK = 5;
  static const int TYPE_GROUP_PIC = 6;

  static const String STR_TEXT = 'text';
  static const String STR_IMG = 'img';
  static const String STR_VIDEO = 'video';
  static const String STR_LINK = 'link';
  static const String STR_GROUP_PIC = 'groupPic';

  NewsDetailItemContentBase();

  String getTypeStr();

  static int getTypeInt(String typeStr) {
    int dataType = TYPE_NONE;
    switch (typeStr) {
      case STR_TEXT:
        dataType = TYPE_TEXT;
        break;
      case STR_IMG:
        dataType = TYPE_IMG;
        break;
      case STR_LINK:
        dataType = TYPE_LINK;
        break;
      case STR_VIDEO:
        dataType = TYPE_VIDEO;
        break;
      case STR_GROUP_PIC:
        dataType = TYPE_GROUP_PIC;
        break;
    }
    return dataType;
  }

  static String getTypeStrFromInt(int typeInt) {
    String typeStr = '';
    switch (typeInt) {
      case TYPE_TEXT:
        typeStr = STR_TEXT;
        break;
      case TYPE_IMG:
        typeStr = STR_IMG;
        break;
      case TYPE_LINK:
        typeStr = STR_LINK;
        break;
      case TYPE_VIDEO:
        typeStr = STR_VIDEO;
        break;
      case TYPE_GROUP_PIC:
        typeStr = STR_GROUP_PIC;
        break;
    }
    return typeStr;
  }

  factory NewsDetailItemContentBase.fromJson(Map<String, dynamic> srcJson) {
    NewsDetailItemContentBase itemContent;
    String type = srcJson['type'] as String;
    switch (getTypeInt(type)) {
      case TYPE_TEXT:
        dynamic infoData = srcJson['info'];
        if (infoData is String) {
          itemContent = NewsDetailItemTxtContent.fromJson(srcJson);
        } else {
          if (infoData is Map<String, dynamic>) {
            dynamic sectionData = infoData['sectionData'];
            if (sectionData != null) {
              itemContent = NewsDetailItemSubjectContent.fromJson(srcJson);
            }
          }
        }
        break;
      case TYPE_IMG:
        itemContent = NewsDetailItemImgContent.fromJson(srcJson);
        break;
      case TYPE_VIDEO:
        itemContent = NewsDetailItemVideoContent.fromJson(srcJson);
        break;
      default:
        itemContent = NewsDetailItemUnSupportContent.fromJson(srcJson);
        break;
    }
    return itemContent;
  }

  Map<String, dynamic> toJson();
}

@JsonSerializable()
class NewsDetailItemTxtContent extends NewsDetailItemContentBase {
  static const int LOCAL_STYLE_TITLE = 1;
  static const int LOCAL_STYLE_SUB_TITLE = 2;
  String info;

  String type;

  int localStyle;

  NewsDetailItemTxtContent(
    this.type,
    this.info,
  );

  factory NewsDetailItemTxtContent.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemTxtContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemTxtContentToJson(this);

  @override
  String getTypeStr() {
    return type;
  }
}


@JsonSerializable()
class NewsDetailItemUnSupportContent extends NewsDetailItemContentBase {
  String type;

  NewsDetailItemUnSupportContent(
      this.type,
      );

  factory NewsDetailItemUnSupportContent.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemUnSupportContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemUnSupportContentToJson(this);

  @override
  String getTypeStr() {
    return type;
  }
}

@JsonSerializable()
class NewsDetailItemVideoContent extends NewsDetailItemContentBase {
  String desc;

  String duration;

  NewsDetailItemImgGroup img;

  String title;

  String type;

  String vid;

  NewsDetailItemVideoContent(
    this.desc,
    this.duration,
    this.img,
    this.title,
    this.type,
    this.vid,
  );

  ImageItem getPreviewImg() {
    ImageItem previewImg;
    if (img != null) {
      if (img.imgurl0 != null && img.imgurl0.isValid()) {
        previewImg = img.imgurl0;
      } else {
        previewImg = img.imgurl1000;
      }
    }
    return previewImg;
  }

  factory NewsDetailItemVideoContent.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemVideoContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemVideoContentToJson(this);

  @override
  String getTypeStr() {
    return type;
  }
}

@JsonSerializable()
class NewsDetailItemImgContent extends NewsDetailItemContentBase {
  String count;

  String face;

  String has180;

  NewsDetailItemImgGroup img;

  String islong;

  String isqrcode;

  String itype;

  String type;

  NewsDetailItemImgContent(
    this.count,
    this.face,
    this.has180,
    this.img,
    this.islong,
    this.isqrcode,
    this.itype,
    this.type,
  );

  ImageItem getPreviewImg() {
    ImageItem previewImg;
    if (img != null) {
      if (img.imgurl0 != null && img.imgurl0.isValid()) {
        previewImg = img.imgurl0;
      } else {
        previewImg = img.imgurl1000;
      }
    }
    return previewImg;
  }

  ImageItem getHDImg() {
    ImageItem hdImg;
    if (img != null) {
      if (img.imgurl1000 != null && img.imgurl1000.isValid()) {
        hdImg = img.imgurl1000;
      } else {
        hdImg = img.imgurl0;
      }
    }
    return hdImg;
  }

  factory NewsDetailItemImgContent.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemImgContentFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemImgContentToJson(this);

  @override
  String getTypeStr() {
    return type;
  }
}

@JsonSerializable()
class NewsDetailItemImgGroup extends Object {
  ImageItem imgurl0;
  ImageItem imgurl1000;

  ImageItem imgurlgif;

  NewsDetailItemImgGroup(
    this.imgurl0,
    this.imgurl1000,
    this.imgurlgif,
  );

  factory NewsDetailItemImgGroup.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailItemImgGroupFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailItemImgGroupToJson(this);
}
