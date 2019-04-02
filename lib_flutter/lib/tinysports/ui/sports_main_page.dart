import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/feed/sports_home_page.dart';
import 'package:lib_flutter/tinysports/main/view/main_navigator_bar.dart';
import 'package:lib_flutter/tinysports/main/view/main_navigator_item_view.dart';
import 'package:lib_flutter/tinysports/news/news_container_page.dart';
import 'package:lib_flutter/tinysports/profile/profile_page.dart';
import 'package:lib_flutter/tinysports/schedule/hot_schedule_list_page.dart';
import 'package:lib_flutter/utils/Loger.dart';

class SportsMainPage extends SportsBasePage {
  static final routeName = 'sports_main';
  static final pageName = 'SportsMainPage';

  final List<int> navigatorItemTypeList = [
    MainNavigatorItemView.ITEM_TYPE_HOME,
    MainNavigatorItemView.ITEM_TYPE_SCHEDULE,
    MainNavigatorItemView.ITEM_TYPE_PROFILE
  ];

  @override
  State<StatefulWidget> createState() => SportsMainPageState();
}

class SportsMainPageState extends SportsBasePageState<SportsMainPage> {
  int selectedNavigatorItemType;

  @override
  void initState() {
    super.initState();
    selectedNavigatorItemType = MainNavigatorItemView.ITEM_TYPE_HOME;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: getAppBar(),
      body: Container(
        child: Column(
          children: <Widget>[
            Expanded(
              child: _getHomeTabContent(),
            ),
//            getSelfDefineNavigatorBar(),
            getBottomNavigationBar(),
          ],
        ),
      ),
    );
  }

  void updateSelectedNavigatorItem(int clickedItemType, bool isSelected) {
    log('-->updateSelectedNavigatorItem(), type=$clickedItemType, isSelected=$isSelected');
    if (clickedItemType != selectedNavigatorItemType) {
      setState(() {
        selectedNavigatorItemType = clickedItemType;
      });
    }
  }

  AppBar getAppBar() {
    return selectedNavigatorItemType == MainNavigatorItemView.ITEM_TYPE_HOME
        ? null
        : AppBar(
            title: Text('腾讯体育Lite'),
          );
  }

  Widget getSelfDefineNavigatorBar() {
    return Container(
      child: MainNavigatorBar(
          widget.navigatorItemTypeList, selectedNavigatorItemType,
          (int clickedItemType, bool isSelected) {
        updateSelectedNavigatorItem(clickedItemType, isSelected);
      }),
    );
  }

  Widget getBottomNavigationBar() {
    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      items: getBottomBarItemList(),
      onTap: onBottomBarSelected,
    );
  }

  List<BottomNavigationBarItem> getBottomBarItemList() {
    List<BottomNavigationBarItem> list = List();
    widget.navigatorItemTypeList.forEach((int itemType) {
      bool isSelected = selectedNavigatorItemType == itemType;
      MainNavigatorItemView itemView =
          MainNavigatorItemView(isSelected, itemType, null);
      list.add(
        BottomNavigationBarItem(
            icon: itemView.getIconWidget(), title: itemView.getTitleWidget()),
      );
    });
    return list;
  }

  void onBottomBarSelected(int selectedIndex) {
    log('-->onBottomBarSelected(), selectedIndex=$selectedIndex');
    updateSelectedNavigatorItem(
        widget.navigatorItemTypeList[selectedIndex], true);
  }

  Widget _getHomeTabContent() {
    Widget tabContent;
    switch (selectedNavigatorItemType) {
      case MainNavigatorItemView.ITEM_TYPE_HOME:
        tabContent = SportsHomePage();
        //20190401007173  资讯
        //20190401005988  资讯
        //20190401001318  组图
        //20180805003682  专题
        //dwz test
//        tabContent = NewsContainerPage('20180805003682');
        break;
      case MainNavigatorItemView.ITEM_TYPE_SCHEDULE:
        tabContent = HotScheduleListPage();
        break;
      case MainNavigatorItemView.ITEM_TYPE_PROFILE:
        tabContent = ProfilePage();
        break;
    }
    return tabContent;
  }

  @override
  String getLogTAG() {
    return 'SportsMainPageState';
  }
}
