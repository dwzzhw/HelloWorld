import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/ui/SportsHomePage.dart';
import 'package:lib_flutter/utils/Loger.dart';

class TinySportsRouteManager {
  List<SportsPageItem> sportPageList;

  void initRouteWidgetList() {
    sportPageList = new List();
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
      resultWidget = SportsHomePage();
    }

    printLog(
        '-->getTargetWidget(), routeName=$routeName , result widget=$resultWidget');
    return resultWidget;
  }
}

class SportsPageItem {
  String routeName;
  String pageName;
  Widget widget;

  SportsPageItem(this.routeName, this.pageName, this.widget);
}