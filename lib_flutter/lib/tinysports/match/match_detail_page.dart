import 'package:flutter/material.dart';
import 'package:flutter/src/rendering/sliver_persistent_header.dart';
import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/app_bar_back_button.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/tinysports/match/match_detail_prepost_page.dart';
import 'package:lib_flutter/tinysports/match/model/match_detail_info_model.dart';
import 'package:lib_flutter/tinysports/match/view/match_detail_img_txt_header_view.dart';
import 'package:lib_flutter/utils/Loger.dart';

class MatchDetailPage extends SportsBasePage {
  final String mid;

  MatchDetailPage(this.mid);

  @override
  State<StatefulWidget> createState() => MatchDetailPageState(mid);

//https://app.sports.qq.com/match/detail?&mid=100000:7155
//https://app.sports.qq.com/comment?&reqnum=20&pageflag=0&targetId=2974411487
//https://app.sports.qq.com/match/detailRelatedInfo?needMatchDetail=0&mid=100000:7155

}

class MatchDetailPageState extends SportsBasePageState<MatchDetailPage> {
  String mid;
  MatchDetailInfoModel matchDetailInfoModel;
  MatchDetailInfo matchDetailInfo;
  String pageTitle;

  MatchDetailPageState(this.mid);

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
    return Scaffold(
      appBar: AppBar(
        leading: AppBarBackButton(),
        title: Center(
          child: Text(pageTitle),
        ),
      ),
      body: _getMatchDetailPageContentWidget(),
    );
  }

  Widget _getMatchDetailPageContentWidget() {
    Widget contentWidget;
    if (!isSuccess) {
      contentWidget = Center(
        child: Text('Fail to get match detail info'),
      );
    } else if (matchDetailInfo == null) {
      contentWidget = Center(
        child: CircularProgressIndicator(),
      );
    } else {
      contentWidget = Container(
        child: CustomScrollView(
          slivers: <Widget>[
            SliverPersistentHeader(
              pinned: true,
//            floating: true,
              delegate: MatchDetailPageSliverPersistentHeaderDelegate(
                MatchDetailImgTxtHeaderView(matchDetailInfo),
//                Text('Header'),
                minHeaderHeight: 60.0,
                maxHeaderHeight: 200.0,
              ),
            ),
            SliverToBoxAdapter(
                child: Container(
              color: Colors.blue[100],
              width: 500,
              height: 700,
              alignment: Alignment.center,
              child: _getMatchDetailSubContentWidget(),
//              child: Text('body'),
            )),
          ],
        ),
      );
    }
    return contentWidget;
  }

  Widget getBottomWidget() {
    return SliverToBoxAdapter(
        child: Container(
      color: Colors.blue[100],
      width: 500,
      height: 700,
      alignment: Alignment.center,
      child: Text('Fixed item in SliverToBoxAdapter'),
    ));
  }

  Widget _getMatchDetailSubContentWidget() {
    Widget subContentWidget = MatchDetailPrePostPage(mid, matchDetailInfo);
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
