import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';
import 'package:lib_flutter/tinysports/base/data/schedule_info.dart';
import 'package:lib_flutter/utils/date_util.dart';

class ScheduleItemView extends StatefulWidget {
  final ScheduleInfo scheduleInfo;

  ScheduleItemView(this.scheduleInfo);

  @override
  State<StatefulWidget> createState() => ScheduleItemViewState(scheduleInfo);
}

class ScheduleItemViewState extends State<ScheduleItemView> {
  ScheduleInfo scheduleInfo;
  MatchInfo matchInfo;

  ScheduleItemViewState(this.scheduleInfo);

  @override
  void initState() {
    super.initState();
    matchInfo = scheduleInfo?.matchInfo;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Container(
          padding: const EdgeInsets.fromLTRB(4, 10, 4, 10),
          child: new Row(
            children: [
              getDateDescSection(matchInfo),
              new Expanded(
                child: getCenterSectionWidget(matchInfo),
              ),
              getMatchStateSection(matchInfo),
            ],
          ),
        ),
        Container(
          padding: EdgeInsets.fromLTRB(4, 0, 4, 0),
          height: 0.5,
          color: Colors.grey[200],
        ),
      ],
    );
  }

  Widget getDateDescSection(MatchInfo matchInfo) {
    return Container(
      width: 100,
      child: Column(
        children: <Widget>[
          Text(
            '${DateUtil.getDateStrPartII(matchInfo.startTime)}',
            style: TextStyle(fontSize: 14),
          ),
          Text(
            '${matchInfo.matchDesc}',
            maxLines: 1,
            overflow: TextOverflow.clip,
            softWrap: true,
            style: TextStyle(fontSize: 12),
          )
        ],
      ),
    );
  }

  Widget getCenterSectionWidget(MatchInfo matchInfo) {
    if (matchInfo.isVsMatch()) {
      return getVsInfoSection(matchInfo);
    } else {
      return getMatchTitleSection(matchInfo.title);
    }
  }

  Widget getVsInfoSection(MatchInfo matchInfo) {
    return Row(
      children: <Widget>[
        Expanded(
          child: Column(
            children: <Widget>[
              getTeamLogoNameSection(matchInfo.leftBadge, matchInfo.leftName),
              getTeamLogoNameSection(matchInfo.rightBadge, matchInfo.rightName),
            ],
          ),
        ),
        getMatchScoreSection(matchInfo),
      ],
    );
    ;
  }

  Widget getTeamLogoNameSection(String logoUrl, String name) {
    return Container(
      padding: EdgeInsets.fromLTRB(5, 2, 1, 2),
      child: Row(
        children: <Widget>[
          Image.network(
            logoUrl,
            width: 20,
            height: 20,
          ),
          Text(
            name,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          )
        ],
      ),
    );
  }

  Widget getMatchTitleSection(String matchTitle) {
    return Container(
      padding: EdgeInsets.fromLTRB(0, 0, 10, 0),
      child: Text(
        matchTitle,
        style: new TextStyle(
          fontSize: 14,
        ),
        maxLines: 2,
      ),
    );
  }

  Widget getMatchScoreSection(MatchInfo matchInfo) {
    bool needScoreArea =
        matchInfo.isLiveFinished() || matchInfo.isLiveOngoing();
    Color scoreTxtColor = matchInfo.isLiveOngoing() ? Colors.red : Colors.black;
    return Container(
      width: 40,
      child: Column(
        children: <Widget>[
          Text(
            needScoreArea ? matchInfo.leftGoal : '-',
            style: TextStyle(
              color: scoreTxtColor,
            ),
          ),
          Text(
            needScoreArea ? matchInfo.rightGoal : '-',
            style: TextStyle(
              color: scoreTxtColor,
            ),
          ),
        ],
      ),
    );
  }

  Widget getMatchStateSection(MatchInfo matchInfo) {
    Color stateTxtColor = matchInfo.isLiveOngoing() ? Colors.red : Colors.grey;
    return Container(
      width: 80,
      child: Column(
        children: <Widget>[
          Text(
            matchInfo.commentator,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: TextStyle(
              fontSize: 10,
              color: stateTxtColor,
            ),
          ),
          Text(
            matchInfo.getLiveTypeDesc(),
            style: TextStyle(
              fontSize: 12,
              color: stateTxtColor,
            ),
          ),
          Text(
            matchInfo.quarter,
            style: TextStyle(
              fontSize: 10,
              color: stateTxtColor,
            ),
          ),
        ],
      ),
    );
  }
}
