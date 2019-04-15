import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/common_view_manager.dart';
import 'package:lib_flutter/tinysports/feed/model/feed_list_ids_detail_model.dart';
import 'package:lib_flutter/utils/common_utils.dart';

class SportsHomeFeedListPage extends SportsBasePage {
  final bool needAppBar;
  final String columnId;

  @override
  State<StatefulWidget> createState() => SportsHomeFeedListPageState(columnId);

  SportsHomeFeedListPage(this.columnId, {this.needAppBar = false});
}

class SportsHomeFeedListPageState
    extends SportsBasePageState<SportsHomeFeedListPage> {
  String columnId;
  List<NewsItem> feedItemDataList;
  FeedListIdsDetailModel feedListModel;

  SportsHomeFeedListPageState(this.columnId);

  @override
  void initState() {
    llog('-->initState()');
    super.initState();
    feedListModel = FeedListIdsDetailModel(columnId, onFetchDataCompleted);
    feedListModel.loadData();
  }

  void onFetchDataCompleted(
      List<NewsItem> newPageData, int pageIndex, bool isPageOver) {
    llog(
        '-->onFetchDataCompleted(), newPageData=$newPageData, pageIndex=$pageIndex, isPageOver=$isPageOver');
    setState(() {
      isSuccess = !CommonUtils.isListEmpty(newPageData) || isPageOver;
      if (isSuccess) {
        if (feedItemDataList == null) {
          feedItemDataList = List();
        }
        if (pageIndex == 0) {
          feedItemDataList.clear();
        }
        feedItemDataList.addAll(newPageData);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    Widget targetWidget;
    if (widget.needAppBar) {
      targetWidget = new Scaffold(
        appBar: new AppBar(
          title: new Text('Home Feed List'),
        ),
        body: _getFeedListPageContentWidget(),
      );
    } else {
      targetWidget = _getFeedListPageContentWidget();
    }
    llog(
        '-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getFeedListPageContentWidget() {
    Widget contentView;
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      contentView = Center(
        child: Text('$tipsMsg'),
      );
    } else if (feedItemDataList != null) {
      if (feedItemDataList.length > 0) {
        contentView = Container(
          padding: EdgeInsets.fromLTRB(CommonViewManager.PAGE_HORIZON_MARGIN, 0,
              CommonViewManager.PAGE_HORIZON_MARGIN, 0),
          child: ListView.builder(
              itemCount: feedItemDataList.length,
              itemBuilder: (BuildContext context, int position) {
                return _getFeedItemWidget(position);
              }),
        );
      } else {
        contentView = Center(
          child: Text('暂无数据'),
        );
      }
    } else {
      contentView = Center(
        child: CircularProgressIndicator(),
      );
    }
    return contentView;
  }

  Widget _getFeedItemWidget(int feedItemIndex) {
    NewsItem feedItemDetail = feedItemDataList[feedItemIndex];
    return CommonViewManager.getNewsItemView(feedItemDetail);
  }

  @override
  String getLogTAG() {
    return 'SportsHomeFeedListPage';
  }
}
