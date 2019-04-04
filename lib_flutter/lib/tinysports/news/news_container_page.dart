import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/app_bar_back_button.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';
import 'package:lib_flutter/tinysports/news/model/news_detail_info_model.dart';
import 'package:lib_flutter/tinysports/news/news_normal_detail_page.dart';
import 'package:lib_flutter/tinysports/news/news_photo_group_page.dart';
import 'package:lib_flutter/tinysports/news/news_special_detail_page.dart';

class NewsContainerPage extends SportsBasePage {
  static final routeName = 'news_detail';
  static final pageName = 'NewsDetailPage';

  final bool needAppBar;
  final String newsId;

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
    llog('-->fetchDataFromModel(), newsDetailInfo=$detailInfo');
    setState(() {
      newsDetailInfo = detailInfo;
    });
  }

  @override
  Widget build(BuildContext context) {
    Widget targetWidget;
    if (widget.needAppBar) {
      targetWidget = Scaffold(
        appBar: AppBar(
          title: Text('News Detail Page'),
        ),
        body: _getNewsDetailPageContentWidget(),
      );
    } else {
      targetWidget = _getNewsDetailPageContentWidget();
    }
    llog('-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getNewsDetailPageContentWidget() {
    llog('-->_getNewsDetailPageContentWidget(), isSuccess=$isSuccess');
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      return Center(
        child: Text('$tipsMsg'),
      );
    } else if (newsDetailInfo != null) {
      return Container(
        child: _getNewsContentWidget(),
      );
    } else {
      return Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  Widget _getNewsContentWidget() {
    Widget contentWidget;
    String aType;
    if (newsDetailInfo != null) {
      aType = newsDetailInfo.atype;
      switch (int.tryParse(aType)) {
        case NewsItem.ITEM_NORMAL:
        case NewsItem.ITEM_VIDEO:
          contentWidget = NewsNormalDetailPage(newsDetailInfo);
          break;
        case NewsItem.ITEM_MULTI_IMG:
          contentWidget = NewsPhotoGroupPage(newsDetailInfo);
          break;
        case NewsItem.ITEM_SPECIAL:
          contentWidget = NewsSpecialDetailPage(newsDetailInfo);
          break;
      }
    }
    llog('-->_getNewsContentWidget(), aType=$aType, widget=$contentWidget');
    if (contentWidget == null) {
      contentWidget = Scaffold(
        appBar: AppBar(
          leading: AppBarBackButton(),
          title: Text('404'),
        ),
        body: Center(
          child: Text('Unsupported News type: $aType'),
        ),
      );
    }
    return contentWidget;
  }

  @override
  String getLogTAG() {
    return 'NewsContainerPage';
  }
}
