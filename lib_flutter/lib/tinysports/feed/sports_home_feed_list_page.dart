import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_index_model.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_list_model.dart';
import 'package:lib_flutter/tinysports/feed/view/feed_item_news_view.dart';

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
    log('-->initState()');
    super.initState();
    _getFeedIndexListFromNet();
  }

  void _getFeedIndexListFromNet() {
    isSuccess = true;
    SportsFeedIndexModel indexModel =
        SportsFeedIndexModel(widget.columnId, (feedIndexData) {
      List<FeedIndexItem> indexList = feedIndexData?.list;
      if (indexList != null) {
        _getFeedListFromNet(indexList);
      } else {
        onFetchDataError('Index list is empty');
      }
    }, (code, errMsg) {
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
    log('__getFeedListFromNet(), ids=$idList');
    SportsFeedListModel listModel = SportsFeedListModel(idList, (feedItemList) {
      log('-->fetch data return, feedItemList=$feedItemList');
      if (feedItemList != null) {
        setState(() {
          feedItemDataList.clear();
          feedItemDataList.addAll(feedItemList);
        });
      } else {
        onFetchDataError('Feed detail list is empty');
      }
    }, (code, errMsg) {
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
    log('-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
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
      return ListView.builder(
          itemCount: feedItemDataList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getFeedItemWidget(position);
          });
    } else {
      return Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  Widget _getFeedItemWidget(int feedItemIndex) {
    NewsItem feedItemDetail = feedItemDataList[feedItemIndex];
    return FeedItemNewsView(feedItemDetail);
  }

  @override
  String getLogTAG() {
    return 'SportsHomeFeedListPage';
  }
}
