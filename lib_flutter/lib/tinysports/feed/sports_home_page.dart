import 'package:flutter/material.dart';
import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/column_info.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/feed/model/column_info_list_model.dart';
import 'package:lib_flutter/tinysports/feed/sports_home_feed_list_page.dart';

///主页上的"首页"页卡
class SportsHomePage extends SportsBasePage {
  static final routeName = 'sports_home';
  static final pageName = 'SportsHomePage';

  @override
  State<StatefulWidget> createState() => SportsHomePageState();
}

class SportsHomePageState extends State<SportsHomePage> {
  List<ColumnInfo> columnInfoList = List();

  @override
  void initState() {
    super.initState();
    _getColumnInfoListFromNet();
  }

  void _getColumnInfoListFromNet() {
    ColumnInfoListModel model =
        ColumnInfoListModel((BaseDataModel<ColumnInfoList> dataModel, int dataType) {
      initColumnList(
          dataModel.mRespData?.list);
    }, (BaseDataModel dataModel, int errCode, String errMsg, int dataType) {
      initColumnList(null);
    });
    model.loadData();
  }

  void initColumnList(List<ColumnInfo> list) {
    setState(() {
      columnInfoList.clear();
      columnInfoList.add(ColumnInfo.localColumnInfo('推荐', ''));
      if (list != null) {
        columnInfoList.addAll(list);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    if (columnInfoList == null || columnInfoList.length == 0) {
      return Center(
        child: CircularProgressIndicator(),
      );
    } else {
      return DefaultTabController(
          length: columnInfoList.length,
          child: Scaffold(
            appBar: AppBar(
              title: Text('腾讯体育Lite'),
              bottom: TabBar(
                isScrollable: true,
                tabs: getTabBarWidgetList(),
              ),
            ),
            body: TabBarView(children: getTabContentWidgetList()),

//          child: Scaffold(
//            appBar: TabBar(
//              labelColor: Colors.red,
//              isScrollable: true,
//              tabs: getTabBarWidgetList(),
//            ),
//            body: TabBarView(children: getTabContentWidgetList()),
          ));

//          child: Scaffold(
//            appBar: AppBar(
//              bottom: TabBar(
//                isScrollable: true,
//                tabs: getTabBarWidgetList(),
//              ),
//            ),
//            body: TabBarView(children: getTabContentWidgetList()),
//          ));
    }
  }

  List<Widget> getTabBarWidgetList() {
    List<Widget> tabList = List();
    columnInfoList.forEach((ColumnInfo columnInfo) {
      tabList.add(Tab(
        text: columnInfo.name,
      ));
    });
    return tabList;
  }

  List<Widget> getTabContentWidgetList() {
    List<Widget> tabList = List();
    columnInfoList.forEach((ColumnInfo columnInfo) {
      tabList.add(SportsHomeFeedListPage(columnInfo.columnId));
    });
    return tabList;
  }
}
