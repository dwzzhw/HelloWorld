import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/match_info.dart';

part 'match_detail_info.g.dart';

@JsonSerializable()
class MatchDetailInfo extends Object {
  MatchInfo matchInfo;

  String programId;

  String liveId;

  String matchPic;

  String onlineCnt;

  String liveIdPictureWord;

  String targetId;

  String updateFrequency;

  String quarterDesc;

  String tabSwitchGuess;

  String isPay;

  String ifHasMultiCamera;

  String leftPropsTeamLogo;

  String rightPropsTeamLogo;

  String categoryId;

  String rightSupport;

  String leftSupport;

  String neuSupport;

  String rightColor;

  String leftColor;

  String ifHasVideo;

  String tabSwitchData;

  String roseNewsId;

  String datah5;

  String hasGuess;

  String onLiveMatchCnt;

  String onLivesDesc;

  String openVideoStat;

  String supportType;

  String useDetailData;

  List<dynamic> tabs;

  String welcomeWord;

  String closeChatRoom;

  String defaultRoomVid;

  String periodDelay;

  MatchDetailInfo(
    this.matchInfo,
    this.programId,
    this.liveId,
    this.matchPic,
    this.onlineCnt,
    this.liveIdPictureWord,
    this.targetId,
    this.updateFrequency,
    this.quarterDesc,
    this.tabSwitchGuess,
    this.isPay,
    this.ifHasMultiCamera,
    this.leftPropsTeamLogo,
    this.rightPropsTeamLogo,
    this.categoryId,
    this.rightSupport,
    this.leftSupport,
    this.neuSupport,
    this.rightColor,
    this.leftColor,
    this.ifHasVideo,
    this.tabSwitchData,
    this.roseNewsId,
    this.datah5,
    this.hasGuess,
    this.onLiveMatchCnt,
    this.onLivesDesc,
    this.openVideoStat,
    this.supportType,
    this.useDetailData,
    this.tabs,
    this.welcomeWord,
    this.closeChatRoom,
    this.defaultRoomVid,
    this.periodDelay,
  );

  factory MatchDetailInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$MatchDetailInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$MatchDetailInfoToJson(this);

  bool isLiveOngoing() {
    return matchInfo != null && matchInfo.isLiveOngoing();
  }

  bool isLivePreStart() {
    return matchInfo != null && matchInfo.isLivePreStart();
  }

  bool isLiveFinished() {
    return matchInfo != null && matchInfo.isLiveFinished();
  }
}
