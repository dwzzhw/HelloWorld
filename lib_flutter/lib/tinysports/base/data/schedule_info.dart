import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';

part 'schedule_info.g.dart';
@JsonSerializable()
class ScheduleInfo extends Object {
  String liveId;

  String isPay;

  String targetId;

  String userNum;

  String updateFrequency;

  String isDisabled;

  String categoryId;

  String matchLevel;

  String coverPicture;

  MatchInfo matchInfo;

  String ifHasPlayback;

  String ifHasHighlights;

  String liveSource;

  ScheduleInfo(
    this.liveId,
    this.isPay,
    this.targetId,
    this.userNum,
    this.updateFrequency,
    this.isDisabled,
    this.categoryId,
    this.matchLevel,
    this.coverPicture,
    this.matchInfo,
    this.ifHasPlayback,
    this.ifHasHighlights,
    this.liveSource,
  );

  factory ScheduleInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$ScheduleInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ScheduleInfoToJson(this);
}
