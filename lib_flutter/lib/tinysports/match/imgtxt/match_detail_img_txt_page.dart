import 'package:flutter/material.dart';
import 'package:lib_flutter/http/multi_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/comment_item.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/view/common_view_manager.dart';
import 'package:lib_flutter/tinysports/comment/data/comment_list_content_info.dart';
import 'package:lib_flutter/tinysports/comment/data/comment_list_page_info.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_related_info.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/data/img_txt_item_detail_info.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/model/img_txt_ids_detail_data_model.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/view/img_txt_commentator_item_view.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/view/img_txt_event_item_view.dart';
import 'package:lib_flutter/tinysports/match/model/match_detail_pre_post_model.dart';
import 'package:lib_flutter/tinysports/match/view/MatchViewManager.dart';
import 'package:lib_flutter/utils/common_utils.dart';

class MatchDetailImgTxtPage extends SportsBasePage {
  final MatchDetailInfo matchDetailInfo;
  final String mid;
  final bool isNestedScrollList;

  MatchDetailImgTxtPage(this.mid, this.matchDetailInfo,
      {this.isNestedScrollList = true});

  @override
  State<StatefulWidget> createState() =>
      MatchDetailImgTxtPageState(mid, matchDetailInfo, isNestedScrollList);
}

class MatchDetailImgTxtPageState
    extends SportsBasePageState<MatchDetailImgTxtPage> {
  String mid;
  MatchDetailInfo matchDetailInfo;
  ImgTxtIdsDetailDataModel idsDetailDataModel;
  List<ImgTxtItemDetail> detailDataList;
  bool isNestedScrollList;

  MatchDetailImgTxtPageState(
      this.mid, this.matchDetailInfo, this.isNestedScrollList);

  @override
  void initState() {
    super.initState();

    idsDetailDataModel = ImgTxtIdsDetailDataModel(mid, onFetchDataCompleted);

    idsDetailDataModel.loadData();
  }

  void onFetchDataCompleted(
      List<ImgTxtItemDetail> newPageData, int pageIndex, bool isPageOver) {
    llog(
        '-->onFetchDataCompleted(), newPageData=$newPageData, pageIndex=$pageIndex, isPageOver=$isPageOver');
    setState(() {
      if (detailDataList == null) {
        detailDataList = List();
      }
      isSuccess = !CommonUtils.isListEmpty(newPageData) || isPageOver;
      if (isSuccess) {
        if (pageIndex == 0) {
          detailDataList.clear();
        }
        detailDataList.addAll(newPageData);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return _getImgTxtPageContentWidget();
  }

  Widget _getImgTxtPageContentWidget() {
    Widget contentWidget;
    if (!isSuccess) {
      contentWidget = Scaffold(
        body: Center(
          child: Text('Fail to get match detail related info'),
        ),
      );
    } else if (detailDataList == null) {
      contentWidget = Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    } else {
      llog(
          '-->_getImgTxtPageContentWidget(), item count=${detailDataList.length}');
      if (isNestedScrollList) {
        contentWidget = SafeArea(
            top: false,
            bottom: false,
            child: Builder(builder: (BuildContext context) {
              return CustomScrollView(
                key: PageStorageKey<String>('match_img_txt_list'),
                slivers: <Widget>[
                  SliverOverlapInjector(
                    handle: NestedScrollView.sliverOverlapAbsorberHandleFor(
                        context),
                  ),
                  SliverPadding(
                    padding: const EdgeInsets.symmetric(
                      vertical: 8.0,
                      horizontal: 16.0,
                    ),
                    sliver: SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (BuildContext context, int index) {
                          return getItemView(detailDataList[index]);
                        },
                        childCount: detailDataList.length,
                      ),
                    ),
                  ),
                ],
              );
            }));
      } else {
        contentWidget = Container(
          padding: EdgeInsets.fromLTRB(CommonViewManager.PAGE_HORIZON_MARGIN, 0,
              CommonViewManager.PAGE_HORIZON_MARGIN, 0),
          child: ListView.builder(
              itemCount: detailDataList.length,
              itemBuilder: (BuildContext context, int index) {
                return getItemView(detailDataList[index]);
              }),
        );
      }
    }
    return contentWidget;
  }

  Widget getItemView(ImgTxtItemDetail itemData) {
    Widget itemView;
    if (itemData.isCommentatorTypeData()) {
      itemView = ImgTxtCommentatorItemView(itemData);
    } else if (itemData.isEventTypeData()) {
      itemView = ImgTxtEventItemView(itemData);
    } else {
      itemView = Text('Unknown data type, cType=${itemData?.ctype}');
    }
    return itemView;
  }

  @override
  String getLogTAG() {
    return 'MatchDetailImgTxtPage';
  }
}
