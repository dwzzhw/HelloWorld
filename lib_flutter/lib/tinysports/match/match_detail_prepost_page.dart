import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/common_view_manager.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_related_info.dart';
import 'package:lib_flutter/tinysports/match/model/match_detail_related_info_model.dart';
import 'package:lib_flutter/tinysports/match/view/MatchViewManager.dart';
import 'package:lib_flutter/utils/common_utils.dart';

class MatchDetailPrePostPage extends SportsBasePage {
  final MatchDetailInfo matchDetailInfo;
  final String mid;

  MatchDetailPrePostPage(this.mid, this.matchDetailInfo);

  @override
  State<StatefulWidget> createState() =>
      MatchDetailPrePostPageState(mid, matchDetailInfo);
}

class MatchDetailPrePostPageState
    extends SportsBasePageState<MatchDetailPrePostPage> {
  String mid;
  MatchDetailInfo matchDetailInfo;
  MatchDetailRelatedInfoModel matchDetailRelatedInfoModel;
  MatchDetailRelatedInfo matchDetailRelatedInfo;

  MatchDetailPrePostPageState(this.mid, this.matchDetailInfo);

  @override
  void initState() {
    super.initState();

    matchDetailRelatedInfoModel = MatchDetailRelatedInfoModel(
        mid, fetchDataFromModel, (int code, String errMsg) {
      onFetchDataError(errMsg);
    });
    matchDetailRelatedInfoModel.loadData();
  }

  void fetchDataFromModel(MatchDetailRelatedInfo info) {
    log('-->fetchDataFromModel(), matchDetailInfo=$info');
    setState(() {
      matchDetailRelatedInfo = info;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _getMatchDetailPrePostPageContentWidget(),
    );
  }

  Widget _getMatchDetailPrePostPageContentWidget() {
    Widget contentWidget;
    if (!isSuccess) {
      contentWidget = Center(
        child: Text('Fail to get match detail related info'),
      );
    } else if (matchDetailRelatedInfo == null) {
      contentWidget = Center(
        child: CircularProgressIndicator(),
      );
    } else {
      List<ViewTypeDataContainer> viewDataList = List();
      RelatedNews relatedNews = matchDetailRelatedInfo.relatedNews;
      if (relatedNews != null && !CommonUtils.isListEmpty(relatedNews.items)) {
        if (!CommonUtils.isEmptyStr(relatedNews.text)) {
          viewDataList.add(ViewTypeDataContainer(
              CommonViewManager.VIEW_TYPE_COMMON_GROUP_HEADER,
              relatedNews.text));
        }
        relatedNews.items.forEach((NewsItem newsItem) {
          viewDataList.add(ViewTypeDataContainer(
              CommonViewManager.getNewsItemViewType(newsItem), newsItem));
        });
      }

      log('-->_getMatchDetailPrePostPageContentWidget(), item count=${viewDataList.length}');
      contentWidget = ListView.builder(
          itemCount: viewDataList.length,
          itemBuilder: (BuildContext context, int index) {
            return MatchViewManager.getMatchView(viewDataList[index]);
          });
    }
    return contentWidget;
  }

  @override
  String getLogTAG() {
    return 'MatchDetailPrePostPage';
  }
}
