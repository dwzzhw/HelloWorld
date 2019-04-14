import 'package:flutter/material.dart';
import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/video_player_view.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_sub_tab_info.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/match_detail_img_txt_page.dart';
import 'package:lib_flutter/tinysports/match/match_detail_prepost_sliver_page.dart';
import 'package:lib_flutter/tinysports/match/model/match_detail_info_model.dart';
import 'package:lib_flutter/tinysports/match/view/match_detail_img_txt_header_view.dart';
import 'package:lib_flutter/utils/Loger.dart';

class MatchDetailContainerPage extends SportsBasePage {
  final String mid;

  MatchDetailContainerPage(this.mid);

  @override
  State<StatefulWidget> createState() => MatchDetailContainerPageState(mid);
}

class MatchDetailContainerPageState
    extends SportsBasePageState<MatchDetailContainerPage> {
//  String mockVideoUrl =
//      'https://flutter.github.io/assets-for-api-docs/assets/videos/butterfly.mp4';
  String mockVideoAssetsPath = 'assets/video/butterfly.mp4';
  double tabBarHeight = 36;
  String mid;
  MatchDetailInfoModel matchDetailInfoModel;
  MatchDetailInfo matchDetailInfo;
  String pageTitle;
  List<String> subTabTypeList;

  MatchDetailContainerPageState(this.mid);

  @override
  void initState() {
    super.initState();
    pageTitle = '';

    matchDetailInfoModel = MatchDetailInfoModel(mid, fetchDataFromModel,
        (BaseDataModel dataModel, int code, String errMsg, int dataType) {
      onFetchDataError(errMsg);
    });
    matchDetailInfoModel.loadData();
  }

  void fetchDataFromModel(
      BaseDataModel<MatchDetailInfo> dataModel, int dataType) {
    llog('-->fetchDataFromModel(), matchDetailInfo=${dataModel.mRespData}');
    setState(() {
      matchDetailInfo = dataModel.mRespData;
      MatchInfo matchInfo = matchDetailInfo?.matchInfo;
      if (matchInfo != null) {
        if (matchInfo.isVsMatch()) {
          pageTitle = matchInfo.leftName + ' VS ' + matchInfo.rightName;
        } else {
          pageTitle = matchInfo.title;
        }
      }
      updateSubTabTypeList();
    });
  }

  void updateSubTabTypeList() {
    if (subTabTypeList != null) {
      subTabTypeList.clear();
    } else {
      subTabTypeList = List();
    }

    subTabTypeList.add(MatchDetailSubTabInfo.TAB_TYPE_PRE_POST_INFO);
    if (matchDetailInfo != null &&
        (matchDetailInfo.isLiveOngoing() || matchDetailInfo.isLiveFinished())) {
      subTabTypeList.add(MatchDetailSubTabInfo.TAB_TYPE_IMG_TXT_LIVE);
//      subTabTypeList.add(MatchDetailSubTabInfo.TAB_TYPE_STAT_DATA);
    }
  }

  @override
  Widget build(BuildContext context) {
    return _getMatchDetailPageContentWidget();
  }

  Widget _getMatchDetailPageContentWidget() {
    Widget contentWidget;
    if (!isSuccess) {
      contentWidget = Scaffold(
        body: Center(
          child: Text('Fail to get match detail info'),
        ),
      );
    } else if (matchDetailInfo == null) {
      contentWidget = Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    } else {
      contentWidget = DefaultTabController(
        length: getSubTabsCnt(),
        child: Scaffold(
          body: NestedScrollView(
            headerSliverBuilder:
                (BuildContext context, bool innerBoxIsScrolled) {
              return <Widget>[
                SliverOverlapAbsorber(
                  handle:
                      NestedScrollView.sliverOverlapAbsorberHandleFor(context),
                  child: SliverAppBar(
                    title: Text(pageTitle),
//                  actions: <Widget>[MaterialDemoDocumentationButton(routeName)],
                    pinned: true,
                    expandedHeight: getHeaderMaxHeight(context),
                    forceElevated: innerBoxIsScrolled,
                    bottom: getTabBarView(),
                    flexibleSpace: SizedBox.expand(
                      child: getHeaderContentView(),
                    ),
                  ),
                ),
              ];
            },
            body: TabBarView(
              children: getSubTabSliverViewList(),
            ),
          ),
        ),
      );
    }
    return contentWidget;
  }

  double getHeaderMaxHeight(BuildContext context) {
    return MediaQuery.of(context).size.width * 9 / 16 + tabBarHeight;
  }

  Widget getHeaderContentView() {
    Widget headerContentView;
    if (matchDetailInfo != null) {
      if (matchDetailInfo.isLiveFinished() || matchDetailInfo.isLiveOngoing()) {
//        String descStr = matchDetailInfo.isLiveFinished() ? '赛事已结束' : '精彩赛事进行中';
//        headerContentView = Center(
//          child: Text(descStr),
//        );
        headerContentView = VideoPlayerView(assetsPath: mockVideoAssetsPath);
      } else {
        headerContentView = MatchDetailImgTxtHeaderView(matchDetailInfo);
      }
    } else {
      headerContentView = Center(
        child: Text('未能正确获取赛事信息'),
      );
    }
    return headerContentView;
  }

  int getSubTabsCnt() {
    return subTabTypeList != null ? subTabTypeList.length : 0;
  }

  Widget getTabBarView() {
    Widget tabBarView;
    if (getSubTabsCnt() > 1) {
      List<Widget> tabList = List();
      subTabTypeList.forEach((String tabType) {
        tabList.add(Tab(
          text: MatchDetailSubTabInfo.getSubTabTitle(tabType),
        ));
      });

      tabBarView = PreferredSize(
          child: Container(
            child: TabBar(
              tabs: tabList,
              labelColor: Colors.blue,
              unselectedLabelColor: Colors.black,
              labelStyle: TextStyle(fontSize: 18),
              unselectedLabelStyle: TextStyle(fontSize: 16),
            ),
            color: Colors.white,
          ),
          preferredSize: new Size(double.infinity, tabBarHeight));
    }
    return tabBarView;
  }

  List<Widget> getSubTabSliverViewList() {
    List<Widget> tabSliverViewList = List();
    subTabTypeList.forEach((String tabType) {
      Widget subContentWidget;
      switch (tabType) {
        case MatchDetailSubTabInfo.TAB_TYPE_PRE_POST_INFO:
          subContentWidget = _getMatchDetailPrePostContentWidget();
          break;
        case MatchDetailSubTabInfo.TAB_TYPE_IMG_TXT_LIVE:
          subContentWidget = _getMatchDetailImgTxtContentWidget();
          break;
        case MatchDetailSubTabInfo.TAB_TYPE_STAT_DATA:
          subContentWidget = _getMockTabContent(tabType);
          break;
      }
      if (subContentWidget != null) {
        tabSliverViewList.add(subContentWidget);
      }
    });
    return tabSliverViewList;
  }

  Widget _getMatchDetailPrePostContentWidget() {
    Widget subContentWidget =
        MatchDetailPrePostSliverPage(mid, matchDetailInfo);
    return subContentWidget;
  }

  Widget _getMatchDetailImgTxtContentWidget() {
    Widget subContentWidget = MatchDetailImgTxtPage(mid, matchDetailInfo);
    return subContentWidget;
  }

  Widget _getMockTabContent(String tabType) {
    return Builder(builder: (BuildContext context) {
      return CustomScrollView(
        key: PageStorageKey<String>('mock_tab_content_$tabType'),
        slivers: <Widget>[
          SliverOverlapInjector(
            handle: NestedScrollView.sliverOverlapAbsorberHandleFor(context),
          ),
          SliverPadding(
            padding: const EdgeInsets.symmetric(
              vertical: 8.0,
              horizontal: 16.0,
            ),
            sliver: SliverFixedExtentList(
              itemExtent: 40,
              delegate: SliverChildBuilderDelegate(
                (BuildContext context, int index) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(
                      vertical: 8.0,
                    ),
                    child: Text(
                        '${MatchDetailSubTabInfo.getSubTabTitle(tabType)}_mock_item_$index'),
                  );
                },
                childCount: 100,
              ),
            ),
          ),
        ],
      );
    });
  }

  @override
  String getLogTAG() {
    return 'MatchDetailPage';
  }
}
