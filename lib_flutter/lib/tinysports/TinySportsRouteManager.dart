import 'package:flutter/material.dart';
import '../tinysports/feed/sports_home_page.dart';
import '../tinysports/match/match_detail_container_page.dart';
import '../tinysports/match/match_detail_page.dart';
import '../tinysports/news/news_container_page.dart';
import '../tinysports/ui/sports_main_page.dart';
import '../utils/Loger.dart';

class TinySportsRouteManager {
  List<SportsPageItem> sportPageList;

  void initRouteWidgetList() {
    sportPageList = new List();
    sportPageList.add(SportsPageItem(
        SportsMainPage.routeName, SportsMainPage.pageName, SportsMainPage()));
    sportPageList.add(SportsPageItem(
        SportsHomePage.routeName, SportsHomePage.pageName, SportsHomePage()));
  }

  Widget getTargetWidget(String routeName) {
    Widget resultWidget;
    if (routeName != null) {
      for (int i = 0; i < sportPageList.length; i++) {
        SportsPageItem targetPage = sportPageList[i];
        if (routeName == targetPage.routeName || routeName == '$i') {
          resultWidget = targetPage.widget;
          break;
        }
      }
    }

    if (resultWidget == null) {
      resultWidget = SportsMainPage();
    }

    printLog(
        '-->getTargetWidget(), routeName=$routeName , result widget=$resultWidget');
    return resultWidget;
  }

  static startNewsDetailPage(BuildContext context, String newsId) {
    Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => NewsContainerPage(
                  newsId,
                )));
  }

  static startMatchDetailPage(BuildContext context, String mid) {
//    Navigator.push(
//        context,
//        MaterialPageRoute(
//            builder: (context) => MatchDetailPage(
//                  mid,
//                )));
    Navigator.push(
        context,
        MaterialPageRoute(
            builder: (context) => MatchDetailContainerPage(
                  mid,
                )));
  }
}

class SportsPageItem {
  String routeName;
  String pageName;
  Widget widget;

  SportsPageItem(this.routeName, this.pageName, this.widget);
}
