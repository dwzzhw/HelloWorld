import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';
import 'package:lib_flutter/tinysports/news/model/news_detail_info_model.dart';
import 'package:lib_flutter/tinysports/news/news_normal_detail_page.dart';

class NewsContainerPage extends SportsBasePage {
  final bool needAppBar;
  String newsId;

  NewsContainerPage(this.newsId, {this.needAppBar = false});

  @override
  State<StatefulWidget> createState() => NewsContainerPageState();
}

class NewsContainerPageState extends SportsBasePageState<NewsContainerPage> {
  NewsDetailInfoModel newsDetailInfoModel;
  NewsDetailInfo newsDetailInfo;

  @override
  void initState() {
    super.initState();
    newsDetailInfoModel = NewsDetailInfoModel(widget.newsId, fetchDataFromModel,
        (errCode, errMsg) {
      onFetchDataError(errMsg);
    });
    newsDetailInfoModel.loadData();
  }

  void fetchDataFromModel(NewsDetailInfo detailInfo) {
    log('-->fetchDataFromModel(), newsDetailInfo=$detailInfo');
    setState(() {
      newsDetailInfo = detailInfo;
    });
  }

  @override
  Widget build(BuildContext context) {
    Widget targetWidget;
    if (widget.needAppBar) {
      targetWidget = new Scaffold(
        appBar: new AppBar(
          title: new Text('News Detail Page'),
        ),
        body: _getNewsDetailPageContentWidget(),
      );
    } else {
      targetWidget = _getNewsDetailPageContentWidget();
    }
    log('-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getNewsDetailPageContentWidget() {
    log('-->_getNewsDetailPageContentWidget(), isSuccess=$isSuccess');
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      return new Center(
        child: Text('$tipsMsg'),
      );
    } else if (newsDetailInfo != null) {
      return Container(
        child: NewsNormalDetailPage(newsDetailInfo),
      );
    } else {
      return new Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  @override
  String getLogTAG() {
    return 'NewsContainerPage';
  }
}
