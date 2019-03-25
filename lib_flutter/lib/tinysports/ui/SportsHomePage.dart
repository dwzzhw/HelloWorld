import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/SportsBasePage.dart';
import 'package:lib_flutter/tinysports/feed/SportsHomeFeedListPage.dart';

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
            Icon(Icons.directions_car),
//            Icon(Icons.directions_transit),
//            Icon(Icons.directions_bike),
          ]),
        ));
  }

  Widget _getHomeTabContent(int tabIndex) {
    Widget tabContent;
    switch (tabIndex) {
      case 0:
        tabContent = SportsHomeFeedListPage(false);
        break;
      case 1:
        tabContent = Center(
          child: Text("Second Tab Content"),
        );
        break;
      case 2:
        tabContent = Center(
          child: Text("Third Tab Content"),
        );
        break;
    }
    return tabContent;
  }
}
