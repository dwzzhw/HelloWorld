import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/SportsBasePage.dart';
import 'package:lib_flutter/tinysports/feed/data/SportsFeedIndexRespPO.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';
import 'package:lib_flutter/tinysports/feed/model/SportsFeedIndexModel.dart';
import 'package:lib_flutter/tinysports/feed/model/sports_feed_list_model.dart';
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

  @override
  void initState() {
    super.initState();
    _getFeedIndexListFromNet();
  }

  void _getFeedIndexListFromNet() {
    SportsFeedIndexModel indexModel = SportsFeedIndexModel();
    indexModel.fetchFeedIndexList().then((indexList) {
//      logd(widget.TAG,
//          '-->fetch feed index data completed, data list=$indexList');
      _getFeedListFromNet(indexList);
    }).catchError((error) {
      logd(widget.TAG, 'error happen when fetch feed index data, error=$error');
    });
  }

  void _getFeedListFromNet(List<FeedIndexItem> feedIndexList) {
    SportsFeedListModel listModel = SportsFeedListModel();
    String idList = '';
    for (int i = 0; i < feedIndexList.length && i < 20; i++) {
      FeedIndexItem indexItem = feedIndexList[i];
      if (indexItem.id.startsWith('80_')) {
        idList += indexItem.id;
        idList += ',';
      }
    }
    logd(widget.TAG, '__getFeedListFromNet(), ids=$idList');

    listModel.fetchFeedList(idList, (feedDetailList) {
      logd(widget.TAG,
          'Fetch feed list detail back, feedDetailList=$feedDetailList');

      setState(() {
        feedItemDataList.clear();
        feedItemDataList.addAll(feedDetailList);
      });
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
    if (feedItemDataList.length > 0) {
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
    return new Padding(
      padding: new EdgeInsets.all(0),
      child: ListTile(
        title: Text('${feedItemDetail.title}'),
        subtitle: Text('${feedItemDetail.abstract}'),
      ),
    );
  }
}
