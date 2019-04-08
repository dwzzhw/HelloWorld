import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/common_view_manager.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_index_model.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_list_model.dart';

class SportsHomeFeedListPage extends SportsBasePage {
  final bool needAppBar;
  final String columnId;

  @override
  State<StatefulWidget> createState() => SportsHomeFeedListPageState();

  SportsHomeFeedListPage(this.columnId, {this.needAppBar = false});
}

class SportsHomeFeedListPageState
    extends SportsBasePageState<SportsHomeFeedListPage> {
  List<NewsItem> feedItemDataList = [];

  @override
  void initState() {
    llog('-->initState()');
    super.initState();
    _getFeedIndexListFromNet();
  }

  void _getFeedIndexListFromNet() {
    isSuccess = true;
    SportsFeedIndexModel indexModel =
        SportsFeedIndexModel(widget.columnId, (dataModel, dataType) {
      List<FeedIndexItem> indexList = dataModel.mRespData?.list;
      if (indexList != null) {
        _getFeedListFromNet(indexList);
      } else {
        onFetchDataError('Index list is empty');
      }
    }, (dataModel, code, errMsg, dataType) {
      onFetchDataError(errMsg);
    });
    indexModel.loadData();
  }

  void _getFeedListFromNet(List<FeedIndexItem> feedIndexList) {
    String idList = '';
    for (int i = 0; i < feedIndexList.length && i < 20; i++) {
      FeedIndexItem indexItem = feedIndexList[i];
      if (indexItem.id.startsWith('80_')) {
        idList += indexItem.id;
        idList += ',';
      }
    }
    llog('__getFeedListFromNet(), ids=$idList');
    SportsFeedListModel listModel =
        SportsFeedListModel(idList, (dataModel, dataType) {
      llog('-->fetch data return, feedItemList=${dataModel.mRespData}');
      if (dataModel != null && dataModel.mRespData != null) {
        setState(() {
          feedItemDataList.clear();
          feedItemDataList.addAll(dataModel.mRespData);
        });
      } else {
        onFetchDataError('Feed detail list is empty');
      }
    }, (dataModel, code, errMsg, dataType) {
      onFetchDataError(errMsg);
    });

    listModel.loadData();
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
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      return Center(
        child: Text('$tipsMsg'),
      );
    } else if (feedItemDataList.length > 0) {
      return Container(
        padding: EdgeInsets.fromLTRB(CommonViewManager.PAGE_HORIZON_MARGIN, 0,
            CommonViewManager.PAGE_HORIZON_MARGIN, 0),
        child: ListView.builder(
            itemCount: feedItemDataList.length,
            itemBuilder: (BuildContext context, int position) {
              return _getFeedItemWidget(position);
            }),
      );
    } else {
      return Center(
        child: CircularProgressIndicator(),
      );
    }
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
