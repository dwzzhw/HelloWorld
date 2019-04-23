import 'package:json_annotation/json_annotation.dart';
import '../../../../tinysports/base/data/video_item.dart';

part 'img_txt_item_detail_info.g.dart';

@JsonSerializable()
class ImgTxtItemDetailInfo extends Object {
  Map<String, ImgTxtItemDetail> detail;

  ImgTxtItemDetailInfo();

  factory ImgTxtItemDetailInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$ImgTxtItemDetailInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ImgTxtItemDetailInfoToJson(this);
}

@JsonSerializable()
class ImgTxtItemDetail extends Object {
  static const String ITEM_TYPE_COMMENTATOR = '1';
  static const String ITEM_TYPE_EVENT = '2';

  String id;

  String ctype;

  String content;

  Commentator commentator;

  String sendTime;

  String topIndex;

  String version;

  String quarter;

  String quarterTime;

  VideoItem video;

  String time;

  String type;

  ImgTxtItemDetail(
    this.id,
    this.ctype,
    this.content,
    this.commentator,
    this.sendTime,
    this.topIndex,
    this.version,
    this.quarter,
    this.quarterTime,
    this.video,
    this.time,
    this.type,
  );

  factory ImgTxtItemDetail.fromJson(Map<String, dynamic> srcJson) =>
      _$ImgTxtItemDetailFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ImgTxtItemDetailToJson(this);

  bool isCommentatorTypeData() {
    return ITEM_TYPE_COMMENTATOR == ctype;
  }

  bool isEventTypeData() {
    return ITEM_TYPE_EVENT == ctype;
  }
}

@JsonSerializable()
class Commentator extends Object {
  String role;

  String nick;

  String logo;

  Commentator(
    this.role,
    this.nick,
    this.logo,
  );

  factory Commentator.fromJson(Map<String, dynamic> srcJson) =>
      _$CommentatorFromJson(srcJson);

  Map<String, dynamic> toJson() => _$CommentatorToJson(this);
}
