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
import 'package:lib_flutter/tinysports/match/model/match_detail_pre_post_model.dart';
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
  MatchDetailPrePostModel prePostDataModel;

  MatchDetailRelatedInfo matchDetailRelatedInfo;
  CommentListPageInfo commentListPageInfo;

  MatchDetailPrePostPageState(this.mid, this.matchDetailInfo);

  @override
  void initState() {
    super.initState();

    prePostDataModel = MatchDetailPrePostModel(
        mid, matchDetailInfo?.targetId, onFetchDataCompleted);

    prePostDataModel.loadData();
  }

  void onFetchDataCompleted(
      MultiDataModel multiDataModel, int dataType, bool success) {
    llog(
        '-->onFetchDataCompleted(), multiDataModel=$multiDataModel, dataType=$dataType, success=$success');
    setState(() {
      if (success && multiDataModel is MatchDetailPrePostModel) {
        matchDetailRelatedInfo = multiDataModel.matchDetailRelatedInfo;
        commentListPageInfo = multiDataModel.commentListPageInfo;
        llog(
            '-->onFetchDataCompleted(), matchDetailRelatedInfo=$matchDetailRelatedInfo, commentListPageInfo=$commentListPageInfo');
      } else {
        onFetchDataError(multiDataModel.lastErrorMsg);
      }
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
      //赛事相关数据
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
      //评论相关数据
      CommentListContentInfo commentListInfo = commentListPageInfo?.comment;
      if (commentListInfo != null &&
          !CommonUtils.isListEmpty(commentListInfo.commentIds) &&
          commentListInfo.common != null) {
        String groupTitle = commentListPageInfo.title;
        if (!CommonUtils.isEmptyStr(groupTitle)) {
          viewDataList.add(ViewTypeDataContainer(
              CommonViewManager.VIEW_TYPE_COMMON_GROUP_HEADER,
              groupTitle + '  ${commentListInfo.commentIds.length}'));
        }
        commentListInfo.commentIds.forEach((String commentId) {
          CommentItem commentItem = commentListInfo.common[commentId];
          if (commentItem != null) {
            viewDataList.add(ViewTypeDataContainer(
                CommonViewManager.VIEW_TYPE_COMMENT_HOST_ITEM, commentItem));
          }
        });
      }

      llog(
          '-->_getMatchDetailPrePostPageContentWidget(), item count=${viewDataList.length}');
      contentWidget = Container(
        padding: EdgeInsets.fromLTRB(CommonViewManager.PAGE_HORIZON_MARGIN, 0,
            CommonViewManager.PAGE_HORIZON_MARGIN, 0),
        child: ListView.builder(
            itemCount: viewDataList.length,
            itemBuilder: (BuildContext context, int index) {
              return MatchViewManager.getMatchView(viewDataList[index]);
            }),
      );
    }
    return contentWidget;
  }

  @override
  String getLogTAG() {
    return 'MatchDetailPrePostPage';
  }
}
