import 'package:flutter/material.dart';
import 'package:lib_flutter/channel/native_channel_manager.dart';
import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/comment_item.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/base/sports_base_stateless_page.dart';
import 'package:lib_flutter/tinysports/base/view/app_bar_back_button.dart';
import 'package:lib_flutter/tinysports/base/view/common_view_manager.dart';
import 'package:lib_flutter/tinysports/comment/data/comment_list_content_info.dart';
import 'package:lib_flutter/tinysports/comment/data/comment_list_page_info.dart';
import 'package:lib_flutter/tinysports/comment/model/comment_list_info_model.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';
import 'package:lib_flutter/tinysports/news/view/news_detail_image_view.dart';
import 'package:lib_flutter/tinysports/news/view/news_detail_text_view.dart';
import 'package:lib_flutter/tinysports/news/view/news_detail_video_view.dart';
import 'package:lib_flutter/utils/common_utils.dart';
import 'package:lib_flutter/utils/date_util.dart';

class NewsNormalDetailPage extends SportsBasePage {
  final NewsDetailInfo newsDetailInfo;

  NewsNormalDetailPage(this.newsDetailInfo);

  @override
  State<StatefulWidget> createState() =>
      NewsNormalDetailPageState(newsDetailInfo);
}

class NewsNormalDetailPageState extends State<NewsNormalDetailPage> {
  static const int NEWS_ITEM_TYPE_TXT = 1;
  static const int NEWS_ITEM_TYPE_IMG = 2;
  static const int NEWS_ITEM_TYPE_VIDEO = 3;
  static const int NEWS_ITEM_TYPE_UNSUPPORTED = 4;

  final NewsDetailInfo newsDetailInfo;
  NativeChannelManager nativeChannelManager;
  CommentListInfoModel commentListModel;
  CommentListPageInfo commentListPageInfo;
  List<ViewTypeDataContainer> viewDataList = List();

  NewsNormalDetailPageState(this.newsDetailInfo);

  @override
  void initState() {
    super.initState();
    initNewsDataList();
    commentListModel = CommentListInfoModel(
        newsDetailInfo?.targetId,
        fetchCommentDataFromModel,
        (BaseDataModel dataModel, int code, String errorMsg, int dataType) {});
    commentListModel.loadData();
  }

  void initNewsDataList() {
    viewDataList.clear();

    if (newsDetailInfo.title != null) {
      NewsDetailItemTxtContent titleContent = NewsDetailItemTxtContent(
          NewsDetailItemContentBase.TYPE_TEXT.toString(), newsDetailInfo.title);
      titleContent.localStyle = NewsDetailItemTxtContent.LOCAL_STYLE_TITLE;
      viewDataList.add(ViewTypeDataContainer(NEWS_ITEM_TYPE_TXT, titleContent));
    }
    String subTitleStr = getSubTitleStr();
    if (subTitleStr != null && subTitleStr.length > 0) {
      NewsDetailItemTxtContent subTitleContent = NewsDetailItemTxtContent(
          NewsDetailItemContentBase.TYPE_TEXT.toString(), subTitleStr);
      subTitleContent.localStyle =
          NewsDetailItemTxtContent.LOCAL_STYLE_SUB_TITLE;
      viewDataList
          .add(ViewTypeDataContainer(NEWS_ITEM_TYPE_TXT, subTitleContent));
    }
    List<NewsDetailItemContentBase> itemList = newsDetailInfo?.content;
    if (!CommonUtils.isListEmpty(itemList)) {
      itemList.forEach((newsItem) {
        if (newsItem is NewsDetailItemTxtContent) {
          viewDataList.add(ViewTypeDataContainer(NEWS_ITEM_TYPE_TXT, newsItem));
        } else if (newsItem is NewsDetailItemImgContent) {
          viewDataList.add(ViewTypeDataContainer(NEWS_ITEM_TYPE_IMG, newsItem));
        } else if (newsItem is NewsDetailItemVideoContent) {
          viewDataList
              .add(ViewTypeDataContainer(NEWS_ITEM_TYPE_VIDEO, newsItem));
        } else {
          viewDataList
              .add(ViewTypeDataContainer(NEWS_ITEM_TYPE_UNSUPPORTED, newsItem));
        }
      });
    }
  }

  void fetchCommentDataFromModel(BaseDataModel commentModel, int dataType) {
    setState(() {
      commentListPageInfo = commentListModel.mRespData;
      if (commentListPageInfo != null) {
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
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: AppBarBackButton(),
        title: Text(''),
      ),
      body: Container(
        padding: EdgeInsets.all(12),
        child: _getNewsDetailListView(),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: showCommentPanel,
        child: Icon(Icons.edit),
      ),
    );
  }

  void showCommentPanel() {
    if (nativeChannelManager == null) {
      nativeChannelManager = NativeChannelManager();
    }
    nativeChannelManager.showNativeCommentPanel();
  }

  Widget _getNewsDetailListView() {
    if (CommonUtils.isListEmpty(viewDataList)) {
      return Container(
        child: Center(
          child: Text('Content is empty'),
        ),
      );
    } else {
      List<NewsDetailItemContentBase> headerList = List();
      if (newsDetailInfo.title != null) {
        NewsDetailItemTxtContent titleContent = NewsDetailItemTxtContent(
            NewsDetailItemContentBase.TYPE_TEXT.toString(),
            newsDetailInfo.title);
        titleContent.localStyle = NewsDetailItemTxtContent.LOCAL_STYLE_TITLE;
        headerList.add(titleContent);
      }
      String subTitleStr = getSubTitleStr();
      if (subTitleStr != null && subTitleStr.length > 0) {
        NewsDetailItemTxtContent subTitleContent = NewsDetailItemTxtContent(
            NewsDetailItemContentBase.TYPE_TEXT.toString(), subTitleStr);
        subTitleContent.localStyle =
            NewsDetailItemTxtContent.LOCAL_STYLE_SUB_TITLE;
        headerList.add(subTitleContent);
      }

      return ListView.builder(
          itemCount: viewDataList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getNewsDetailContentItemView(viewDataList[position]);
          });
    }
  }

  Widget _getNewsDetailContentItemView(ViewTypeDataContainer itemContent) {
    Widget resultView;

    switch (itemContent.viewType) {
      case NEWS_ITEM_TYPE_TXT:
        resultView = NewsDetailTextView(itemContent.data);
        break;
      case NEWS_ITEM_TYPE_IMG:
        resultView = NewsDetailImageView(itemContent.data);
        break;
      case NEWS_ITEM_TYPE_VIDEO:
        resultView = NewsDetailVideoView(itemContent.data);
        break;
      case NEWS_ITEM_TYPE_UNSUPPORTED:
        resultView =
            Text('Unsupported data, type=${itemContent.data?.getTypeStr()}');
        break;
    }
    if (resultView == null) {
      resultView = CommonViewManager.getCommonView(
          itemContent.viewType, itemContent.data);
    }
    return resultView;
  }

  String getSubTitleStr() {
    String subTitle = '';
    if (newsDetailInfo != null) {
      if (newsDetailInfo.source != null) {
        subTitle += newsDetailInfo.source;
      }
      if (newsDetailInfo.pub_time != null) {
        if (subTitle != '') {
          subTitle += '    ';
        }
        subTitle +=
            DateUtil.getDateMonthDayHourMinPart(newsDetailInfo.pub_time);
      }
    }
    return subTitle;
  }
}
