import 'package:json_annotation/json_annotation.dart';

part 'match_info.g.dart';

@JsonSerializable()
class MatchInfo extends Object {
  String matchType;

  String mid;

  String leftId;

  String leftTeamId;

  String leftOldId;

  String leftName;

  String leftBadge;

  String leftGoal;

  String isLeftTeamAble;

  String rightId;

  String rightTeamId;

  String rightOldId;

  String rightName;

  String rightBadge;

  String rightGoal;

  String isRightTeamAble;

  String matchDesc;

  String startTime;

  String title;

  String logo;

  String matchPeriod;

  String livePeriod;

  String quarter;

  String quarterTime;

  String liveType;

  String lTeamUrl;

  String rTeamUrl;

  String phaseText;

  String phaseP;

  String matchInfoVersion;

  String groupName;

  String homeNormalGoal;

  String awayNormalGoal;

  String homeShootOutGoal;

  String awayShootOutGoal;

  String isShootOut;

  String extraQuarterDesc;

  String extraQuarterDesc_h;

  String commentator;

  MatchInfo(
    this.matchType,
    this.mid,
    this.leftId,
    this.leftTeamId,
    this.leftOldId,
    this.leftName,
    this.leftBadge,
    this.leftGoal,
    this.isLeftTeamAble,
    this.rightId,
    this.rightTeamId,
    this.rightOldId,
    this.rightName,
    this.rightBadge,
    this.rightGoal,
    this.isRightTeamAble,
    this.matchDesc,
    this.startTime,
    this.title,
    this.logo,
    this.matchPeriod,
    this.livePeriod,
    this.quarter,
    this.quarterTime,
    this.liveType,
    this.lTeamUrl,
    this.rTeamUrl,
    this.phaseText,
    this.phaseP,
    this.matchInfoVersion,
    this.groupName,
    this.homeNormalGoal,
    this.awayNormalGoal,
    this.homeShootOutGoal,
    this.awayShootOutGoal,
    this.isShootOut,
    this.extraQuarterDesc,
    this.extraQuarterDesc_h,
    this.commentator,
  );

  factory MatchInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$MatchInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$MatchInfoToJson(this);
}
