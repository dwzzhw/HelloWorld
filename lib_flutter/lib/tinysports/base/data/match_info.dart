import 'package:json_annotation/json_annotation.dart';

part 'match_info.g.dart';

@JsonSerializable()
class MatchInfo extends Object {
  static final String TAG = 'MatchInfo';

  // 赛事类型, 1足球、2篮球、3综合赛事、4非对阵赛事
  static final String MATCH_TYPE_FOOTBALL = '1';
  static final String MATCH_TYPE_BASKETBALL = '2';
  static final String MATCH_TYPE_GENERAL = '3';
  static final String MATCH_TYPE_NON_VS = '4';

  // 比赛状态:0未开始、1进行中、2已结束、3比赛前延期、4比赛中延期、5取消
  static final String MATCH_PERIOD_PRESTART = '0';
  static final String MATCH_PERIOD_ONGOING = '1';
  static final String MATCH_PERIOD_FINISHED = '2';
  static final String MATCH_PERIOD_POSTPONED_PRE_MATCH = '3';
  static final String MATCH_PERIOD_POSTPONED_IN_MATCH = '4';
  static final String MATCH_PERIOD_CANCEL = '5';

  // 直播状态 0未开始、1进行中、2已结束，存在任何一种直播即认为正在直播中
  static final String LIVE_PERIOD_PRESTART = '0';
  static final String LIVE_PERIOD_ONGOING = '1';
  static final String LIVE_PERIOD_FINISHED = '2';

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

  bool isVsMatch() {
    return MATCH_TYPE_NON_VS != '$matchType';
  }

  bool isLivePreStart() {
    return livePeriod == LIVE_PERIOD_PRESTART;
  }

  bool isLiveOngoing() {
    return livePeriod == LIVE_PERIOD_ONGOING;
  }

  bool isLiveFinished() {
    return livePeriod == LIVE_PERIOD_FINISHED;
  }

  String getLiveTypeDesc() {
    String desc = '';
    if (livePeriod == LIVE_PERIOD_FINISHED) {
      desc = '已结束';
    } else if (livePeriod == LIVE_PERIOD_ONGOING) {
      desc = '直播中';
    } else if (livePeriod == LIVE_PERIOD_PRESTART) {
      desc = '未开始';
    }
    return desc;
  }
}
