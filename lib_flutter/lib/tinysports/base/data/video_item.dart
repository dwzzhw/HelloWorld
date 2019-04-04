import 'package:json_annotation/json_annotation.dart';

part 'video_item.g.dart';

@JsonSerializable()
class VideoItem extends Object {
  String type;

  String checkUpTime;

  String title;

  String secondTitle;

  String videoCate;

  String cateDesc;

  String desc;

  String pic;

  String duration;

  String view;

  String covers;

  String playUrl;

  String isFullView;

  String category;

  String uploadQq;

  String isPay;

  String tag;

  String matchIndex;

  String pic2;

  String vid;

  VideoItem(
    this.type,
    this.checkUpTime,
    this.title,
    this.secondTitle,
    this.videoCate,
    this.cateDesc,
    this.desc,
    this.pic,
    this.duration,
    this.view,
    this.covers,
    this.playUrl,
    this.isFullView,
    this.category,
    this.uploadQq,
    this.isPay,
    this.tag,
    this.matchIndex,
    this.pic2,
    this.vid,
  );

  factory VideoItem.fromJson(Map<String, dynamic> srcJson) =>
      _$VideoItemFromJson(srcJson);

  Map<String, dynamic> toJson() => _$VideoItemToJson(this);
}
