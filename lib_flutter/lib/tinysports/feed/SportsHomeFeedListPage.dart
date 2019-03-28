import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/SportsBasePage.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_index_model.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_list_model.dart';
import 'package:lib_flutter/tinysports/feed/view/feed_item_news_view.dart';
import 'package:lib_flutter/utils/Loger.dart';

class SportsHomeFeedListPage extends SportsBasePage {
  final String TAG = "SportsHomeFeedListPage";
  final bool needAppBar;

  @override
  State<StatefulWidget> createState() => SportsHomeFeedListPageState();

  SportsHomeFeedListPage(this.needAppBar);
}

class SportsHomeFeedListPageState extends State<SportsHomeFeedListPage> {
  List<FeedItemDetailInfo> feedItemDataList = [];
  bool isSuccess = true;
  String errTipsMsg;

  @override
  void initState() {
    super.initState();
    _getFeedIndexListFromNet();
  }

  void _getFeedIndexListFromNet() {
    isSuccess = true;
    SportsFeedIndexModel indexModel = SportsFeedIndexModel((feedIndexData) {
      List<FeedIndexItem> indexList = feedIndexData?.list;
      if (indexList != null) {
        _getFeedListFromNet(indexList);
      } else {
        _onFetchDataError('Index list is empty');
      }
    }, (code, errMsg) {
      _onFetchDataError(errMsg);
    });
//    indexModel.setCompleteCallbackFunc(() {
//      List<FeedIndexItem> indexList = indexModel.getFeedIndexList();
//      logd(widget.TAG,
//          '-->fetch feed index data completed, data list=$indexList');
//      _getFeedListFromNet(indexList);
//    });
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
    logd(widget.TAG, '__getFeedListFromNet(), ids=$idList');
    SportsFeedListModel listModel = SportsFeedListModel(idList, (feedItemList) {
      if (feedItemList == null) {
        setState(() {
          feedItemDataList.clear();
          feedItemDataList.addAll(feedItemList);
        });
      } else {
        _onFetchDataError('Item list is empty');
      }
    }, (code, errMsg) {
      _onFetchDataError(errMsg);
    });

    listModel.loadData();

//    listModel.fetchFeedList(idList, (feedDetailList) {
//      logd(widget.TAG,
//          'Fetch feed list detail back, feedDetailList=$feedDetailList');
//    });
  }

  void _onFetchDataError(String errorMsg) {
    logd(widget.TAG, '-->_onFetchDataError(), errorMsg=$errorMsg');
    setState(() {
      isSuccess = false;
      errTipsMsg = errorMsg;
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
    logd('${widget.TAG}',
        '-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getFeedListPageContentWidget() {
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      new Center(
        child: Text('$tipsMsg'),
      );
    } else if (feedItemDataList.length > 0) {
      return new ListView.builder(
          itemCount: feedItemDataList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getFeedItemWidget(position);
          });
    } else {
      return new Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  Widget _getFeedItemWidget(int feedItemIndex) {
    FeedItemDetailInfo feedItemDetail = feedItemDataList[feedItemIndex];
    return FeedItemNewsView(feedItemDetail);
  }
}
