import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/SportsBasePage.dart';
import 'package:lib_flutter/utils/Loger.dart';

class SportsHomeFeedListPage extends SportsBasePage {
  final String TAG = "SportsHomeFeedListPage";
  final bool needAppBar;

  @override
  State<StatefulWidget> createState() => SportsHomeFeedListPageState();

  SportsHomeFeedListPage(this.needAppBar);
}

class SportsHomeFeedListPageState extends State<SportsHomeFeedListPage> {
  List feedItemDataList = [];

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    feedItemDataList.add("1");
    feedItemDataList.add("2");
    feedItemDataList.add("3");

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
    return new Padding(
      padding: new EdgeInsets.all(10),
      child: Text('Feed Item At $feedItemIndex'),
    );
  }
}
