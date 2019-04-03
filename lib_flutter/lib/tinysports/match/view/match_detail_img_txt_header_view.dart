import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/utils/common_utils.dart';
import 'package:lib_flutter/utils/date_util.dart';

class MatchDetailImgTxtHeaderView extends StatelessWidget {
  static const String TAG = 'MatchDetailImgTxtHeaderView';
  final MatchDetailInfo matchDetailInfo;

  MatchDetailImgTxtHeaderView(this.matchDetailInfo);

  @override
  Widget build(BuildContext context) {
    Widget resultWidget;
    MatchInfo matchInfo = matchDetailInfo?.matchInfo;
    if (matchInfo != null) {
      if (matchInfo.isVsMatch()) {
        resultWidget = Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            getTeamLogoNameView(matchInfo.leftBadge, matchInfo.leftName),
            Container(
              padding: EdgeInsets.fromLTRB(10, 0, 10, 0),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  getMatchDateTimeLiveTypeView(),
                  getVSAreaWidget(),
                  getMatchDescView(),
                ],
              ),
            ),
            getTeamLogoNameView(matchInfo.rightBadge, matchInfo.rightName),
          ],
        );
      } else {
        resultWidget = Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: <Widget>[
              getMatchDateTimeLiveTypeView(),
              getMatchTitleView(),
              getMatchDescView(),
            ],
          ),
        );
      }
    }
    if (resultWidget == null) {
      resultWidget = Center(
        child: Text('Bad Match Info'),
      );
    } else {
//      Color startColor = Color(0xFF1589CC);
//      Color endColor = Color(0xFF0F6799);
      Color startColor = Colors.blue;
      Color endColor = Colors.green;

      if (!CommonUtils.isEmptyStr(matchDetailInfo.leftColor) &&
          !CommonUtils.isEmptyStr(matchDetailInfo.rightColor)) {
        startColor = Color(int.tryParse(
            matchDetailInfo.leftColor.replaceAll(
                '#', matchDetailInfo.leftColor.length < 8 ? 'ff' : ''),
            radix: 16));
        endColor = Color(int.tryParse(
            matchDetailInfo.rightColor.replaceAll(
                '#', matchDetailInfo.rightColor.length < 8 ? 'ff' : ''),
            radix: 16));
      }
      resultWidget = Container(
        height: 150,
        decoration: BoxDecoration(
//          color: Colors.blue,
//          border: new Border.all(color: Colors.yellow, width: 5.0,),
//          borderRadius: new BorderRadius.all(new Radius.circular(50.0)),
//          gradient: new LinearGradient(colors: [Colors.blue, Colors.green]),
          gradient: new LinearGradient(
              colors: [startColor, endColor],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight),
        ),
        child: resultWidget,
      );
    }

    return resultWidget;
  }

  Widget getTeamLogoNameView(String logoUrl, String teamName) {
    return Container(
      padding: EdgeInsets.all(12),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          CachedNetworkImage(
            imageUrl: logoUrl,
            width: 60,
            height: 60,
          ),
          Text(
            teamName,
            style: TextStyle(fontSize: 14, color: Colors.white),
          )
        ],
      ),
    );
  }

  Widget getVSAreaWidget() {
    Widget vsWidget;
    MatchInfo matchInfo = matchDetailInfo?.matchInfo;
    if (matchInfo != null) {
      double fontSize;
      Color fontColor;
      if (matchInfo.isLiveOngoing() || matchInfo.isLiveFinished()) {
        fontSize = 24;
        fontColor = matchInfo.isLiveOngoing() ? Colors.red : Colors.black;
        vsWidget = Row(
          children: <Widget>[
            Text(
              matchInfo.leftGoal,
              style: TextStyle(fontSize: fontSize, color: fontColor),
            ),
            Text(
              '  :  ',
              style: TextStyle(fontSize: 26, color: Colors.black),
            ),
            Text(
              matchInfo.rightGoal,
              style: TextStyle(fontSize: fontSize, color: fontColor),
            ),
          ],
        );
      } else {
        vsWidget = Text(
          'VS',
          style: TextStyle(fontSize: 28, color: Colors.white),
        );
      }
    }
    return vsWidget;
  }

  Widget getMatchDateTimeLiveTypeView() {
    String result = '';
    MatchInfo matchInfo = matchDetailInfo?.matchInfo;
    if (matchInfo != null) {
      result = DateUtil.getDateHourMinutePart(matchInfo.startTime);
      String liveTypeDesc = matchInfo.getLiveTypeDesc();
      if (!CommonUtils.isEmptyStr(liveTypeDesc)) {
        result = result + '  ' + liveTypeDesc;
      }
    }
    return Container(
      padding: EdgeInsets.fromLTRB(10, 0, 10, 10),
      child: Text(
        result,
        style: TextStyle(fontSize: 14, color: Colors.white),
      ),
    );
  }

  Widget getMatchDescView() {
    return Container(
      padding: EdgeInsets.fromLTRB(0, 20, 0, 10),
      child: Text(
        matchDetailInfo.matchInfo.matchDesc,
        style: TextStyle(fontSize: 12, color: Colors.white70),
      ),
    );
  }

  Widget getMatchTitleView() {
    return Text(
      matchDetailInfo.matchInfo.title,
      style: TextStyle(fontSize: 18, color: Colors.white),
    );
  }
}
