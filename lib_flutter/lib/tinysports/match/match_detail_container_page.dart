import 'package:flutter/material.dart';
import 'package:flutter/src/rendering/sliver_persistent_header.dart';
import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/app_bar_back_button.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/tinysports/match/match_detail_prepost_page.dart';
import 'package:lib_flutter/tinysports/match/match_detail_prepost_sliver_page.dart';
import 'package:lib_flutter/tinysports/match/model/match_detail_info_model.dart';
import 'package:lib_flutter/tinysports/match/view/match_detail_img_txt_header_view.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/utils/common_utils.dart';

class MatchDetailContainerPage extends SportsBasePage {
  final String mid;

  MatchDetailContainerPage(this.mid);

  @override
  State<StatefulWidget> createState() => MatchDetailContainerPageState(mid);
}

class MatchDetailContainerPageState
    extends SportsBasePageState<MatchDetailContainerPage> {
  String mid;
  MatchDetailInfoModel matchDetailInfoModel;
  MatchDetailInfo matchDetailInfo;
  String pageTitle;

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
    });
  }

  @override
  Widget build(BuildContext context) {
    return _getMatchDetailPageContentWidget();
  }

  List<Widget> getSubTabsList() {
    List<Widget> tabTitleList = [
      Tab(
        text: '赛事回顾',
      ),
      Tab(
        text: 'Tab 02',
      ),
      Tab(
        text: 'Tab 03',
      )
    ];
    return tabTitleList;
  }

  int getSubTabsCnt() {
    List<Widget> tabsList = getSubTabsList();
    return tabsList != null ? tabsList.length : 0;
  }

  Widget getTabBarView() {
    Widget tabBarView;
    if (getSubTabsCnt() > 1) {
      tabBarView = PreferredSize(
          child: Container(
            child: TabBar(
              tabs: getSubTabsList(),
            ),
            color: Colors.blue,
          ),
          preferredSize: new Size(double.infinity, 46.0));
    }
    return tabBarView;
  }

  List<Widget> getSubTabSliverViewList() {
    List<Widget> tabSliverViewList = List();
    tabSliverViewList.add(_getMatchDetailPrePostContentWidget());
//    tabSliverViewList.add(_getMatchDetailPageContentWidget());

//    return tabSliverViewList;

    tabSliverViewList.addAll(_allPages.keys.map<Widget>((_Page page) {
      return Builder(builder: (BuildContext context) {
        return CustomScrollView(
          key: PageStorageKey<_Page>(page),
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
                itemExtent: _CardDataItem.height,
                delegate: SliverChildBuilderDelegate(
                  (BuildContext context, int index) {
                    final _CardData data = _allPages[page][index];
                    return Padding(
                      padding: const EdgeInsets.symmetric(
                        vertical: 8.0,
                      ),
                      child: _CardDataItem(
                        page: page,
                        data: data,
                      ),
                    );
                  },
                  childCount: _allPages[page].length,
                ),
              ),
            ),
          ],
        );
      });
    }).toList());
    return tabSliverViewList;
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
      List<Widget> tabViewList = getSubTabSliverViewList();
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
                    expandedHeight: 220.0,
                    forceElevated: innerBoxIsScrolled,
                    bottom: getTabBarView(),
                    flexibleSpace: SizedBox.expand(
                      child: MatchDetailImgTxtHeaderView(matchDetailInfo),
                    ),
                  ),
                ),
              ];
            },
            body: TabBarView(
              children: tabViewList,
            ),
          ),
        ),
      );
    }
    return contentWidget;
  }

  Widget _getMatchDetailPrePostContentWidget() {
    Widget subContentWidget =
        MatchDetailPrePostSliverPage(mid, matchDetailInfo);
    return subContentWidget;
  }

  @override
  String getLogTAG() {
    return 'MatchDetailPage';
  }
}

class MatchDetailPageSliverPersistentHeaderDelegate
    implements SliverPersistentHeaderDelegate {
  static const String TAG = "MatchDetailHeaderDelegate";
  final double minHeaderHeight;
  final double maxHeaderHeight;
  final Widget headerContentWidget;

  MatchDetailPageSliverPersistentHeaderDelegate(this.headerContentWidget,
      {this.minHeaderHeight = 70, this.maxHeaderHeight = 250});

  @override
  Widget build(
      BuildContext context, double shrinkOffset, bool overlapsContent) {
    Loger.d(TAG,
        '-->buid(), headerContentWidget=$headerContentWidget， shrinkOffset=$shrinkOffset');
//    return Stack(
//      fit: StackFit.expand,
//      children: <Widget>[
//        Container(
//          child: headerContentWidget,
//        ),
//      ],
//    );
    return SizedBox.expand(
      child: headerContentWidget,
    );
  }

  @override
  double get maxExtent => maxHeaderHeight;

  @override
  double get minExtent => minHeaderHeight;

  @override
  bool shouldRebuild(SliverPersistentHeaderDelegate oldDelegate) {
    bool needRebuild = false;
    if (oldDelegate is MatchDetailPageSliverPersistentHeaderDelegate) {
      needRebuild = oldDelegate.minHeaderHeight != minHeaderHeight ||
          oldDelegate.maxHeaderHeight != maxHeaderHeight ||
          oldDelegate.headerContentWidget != headerContentWidget;
    } else {
      needRebuild = true;
    }
    Loger.d(TAG, '-->shouldRebuild(), needRebuild=$needRebuild');
    return needRebuild;
  }

  @override
  FloatingHeaderSnapConfiguration get snapConfiguration => null;
}

const String _kGalleryAssetsPackage = 'flutter_gallery_assets';

class _Page {
  _Page({this.label});

  final String label;

  String get id => label[0];

  @override
  String toString() => '$runtimeType("$label")';
}

class _CardData {
  const _CardData({this.title, this.imageAsset, this.imageAssetPackage});

  final String title;
  final String imageAsset;
  final String imageAssetPackage;
}

final Map<_Page, List<_CardData>> _allPages = <_Page, List<_CardData>>{
  _Page(label: 'HOME'): <_CardData>[
    const _CardData(
      title: 'Flatwear',
      imageAsset: 'products/flatwear.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Pine Table',
      imageAsset: 'products/table.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Blue Cup',
      imageAsset: 'products/cup.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Tea Set',
      imageAsset: 'products/teaset.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Desk Set',
      imageAsset: 'products/deskset.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Blue Linen Napkins',
      imageAsset: 'products/napkins.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Planters',
      imageAsset: 'products/planters.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Kitchen Quattro',
      imageAsset: 'products/kitchen_quattro.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Platter',
      imageAsset: 'products/platter.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
  ],
  _Page(label: 'APPAREL'): <_CardData>[
    const _CardData(
      title: 'Cloud-White Dress',
      imageAsset: 'products/dress.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Ginger Scarf',
      imageAsset: 'products/scarf.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
    const _CardData(
      title: 'Blush Sweats',
      imageAsset: 'products/sweats.png',
      imageAssetPackage: _kGalleryAssetsPackage,
    ),
  ],
};

class _CardDataItem extends StatelessWidget {
  const _CardDataItem({this.page, this.data});

  static const double height = 272.0;
  final _Page page;
  final _CardData data;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          mainAxisAlignment: MainAxisAlignment.start,
          children: <Widget>[
            Align(
              alignment:
                  page.id == 'H' ? Alignment.centerLeft : Alignment.centerRight,
              child: CircleAvatar(child: Text('${page.id}')),
            ),
            SizedBox(
              width: 144.0,
              height: 144.0,
              child: Image.asset(
                data.imageAsset,
                package: data.imageAssetPackage,
                fit: BoxFit.contain,
              ),
            ),
            Center(
              child: Text(
                data.title,
                style: Theme.of(context).textTheme.title,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
