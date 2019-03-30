import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/SportsBasePage.dart';
import 'package:lib_flutter/tinysports/feed/sports_home_feed_list_page.dart';
import 'package:lib_flutter/tinysports/profile/profile_page.dart';
import 'package:lib_flutter/tinysports/schedule/hot_schedule_list_page.dart';

class SportsHomePage extends SportsBasePage {
  static final routeName = 'sports_home';
  static final pageName = 'SportsHomePage';

  @override
  State<StatefulWidget> createState() => SportsHomePageState();
}

class SportsHomePageState extends State<SportsHomePage> {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
        length: 3,
        child: Scaffold(
          appBar: AppBar(
            bottom: TabBar(
              tabs: [
                Tab(
                  icon: Icon(Icons.directions_car),
                ),
                Tab(
                  icon: Icon(Icons.directions_transit),
                ),
                Tab(
                  icon: Icon(Icons.directions_bike),
                ),
              ],
            ),
            title: Text('SportHomePage'),
          ),
          body: TabBarView(children: [
            _getHomeTabContent(0),
            _getHomeTabContent(1),
            _getHomeTabContent(2),
//            Icon(Icons.directions_car),
//            Icon(Icons.directions_transit),
//            Icon(Icons.directions_bike),
          ]),
        ));
  }

  Widget _getHomeTabContent(int tabIndex) {
    Widget tabContent;
    switch (tabIndex) {
      case 0:
        tabContent = Center(
          child: Text("Feed List Tab Content"),
        );
//        tabContent = SportsHomeFeedListPage(false);
        break;
      case 1:
        tabContent = Center(
          child: Text("Schedule List Tab Content"),
        );
//        tabContent = HotScheduleListPage(false);
        break;
      case 2:
//        tabContent = Center(
//          child: Text("Profile Tab Content"),
//        );
        tabContent = ProfilePage(false);
        break;
    }
    return tabContent;
  }
}
