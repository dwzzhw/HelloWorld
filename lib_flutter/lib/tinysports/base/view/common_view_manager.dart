import 'package:flutter/material.dart';
import '../../../tinysports/base/data/comment_item.dart';
import '../../../tinysports/base/data/news_item.dart';
import '../../../tinysports/base/data/schedule_info.dart';
import '../../../tinysports/base/view/comment_item_view.dart';
import '../../../tinysports/base/view/common_group_header_view.dart';
import '../../../tinysports/base/view/news_list_item_view.dart';
import '../../../tinysports/base/view/schedule_item_view.dart';

class CommonViewManager {
  static const String TAG = 'CommonViewManager';

  static const double PAGE_HORIZON_MARGIN = 10;

  static const int VIEW_TYPE_COMMON_NONE = 10000;
  static const int VIEW_TYPE_COMMON_GROUP_HEADER = 10001;

  static const int VIEW_TYPE_NEWS_ITEM_NONE_IMG = 10100;
  static const int VIEW_TYPE_NEWS_ITEM_ONE_IMG = 10101;
  static const int VIEW_TYPE_NEWS_ITEM_THREE_IMG = 10102;

  static const int VIEW_TYPE_SCHEDULE_VS = 10120;
  static const int VIEW_TYPE_SCHEDULE_NON_VS = 10121;

  static const int VIEW_TYPE_COMMENT_HOST_ITEM = 10140;

  static Widget getCommonView(int viewType, dynamic dataObj) {
    Widget resultView;
    switch (viewType) {
      case VIEW_TYPE_COMMON_NONE:
        resultView = getNoneView(dataObj);
        break;
      case VIEW_TYPE_COMMON_GROUP_HEADER:
        if (dataObj is String) {
          resultView = CommonGroupHeaderView(dataObj);
        }
        break;
      case VIEW_TYPE_NEWS_ITEM_ONE_IMG:
      case VIEW_TYPE_NEWS_ITEM_THREE_IMG:
        if (dataObj is NewsItem) {
          resultView = NewsListItemView(dataObj);
        }
        break;
      case VIEW_TYPE_SCHEDULE_VS:
      case VIEW_TYPE_SCHEDULE_VS:
        if (dataObj is ScheduleInfo) {
          resultView = ScheduleItemView(dataObj);
        }
        break;
      case VIEW_TYPE_COMMENT_HOST_ITEM:
        if (dataObj is CommentItem) {
          resultView = getCommentHostItemView(dataObj);
        }
        break;
    }
    return resultView;
  }

  static Widget getNoneView(dynamic dataObj) {
    String tipsStr = 'None view';
    if (dataObj is NewsItem) {
      tipsStr = 'Unsupported News Item, atype=${dataObj.atype}';
    }
    return Container(
//      width: 0,
//      height: 0,
      child: Text(tipsStr),
    );
  }

  static Widget getCommentHostItemView(CommentItem commentItem) {
    return CommentItemView(commentItem);
  }

  static Widget getNewsItemView(NewsItem newsItem) {
    return getCommonView(getNewsItemViewType(newsItem), newsItem);
  }

  static int getNewsItemViewType(NewsItem newsItem) {
    int itemViewType = VIEW_TYPE_COMMON_NONE;
    switch (int.tryParse(newsItem?.atype)) {
      case NewsItem.ITEM_MULTI_IMG:
        itemViewType = VIEW_TYPE_NEWS_ITEM_THREE_IMG;
        break;
      case NewsItem.ITEM_SPECIAL:
      case NewsItem.ITEM_VIDEO:
      case NewsItem.ITEM_VIDEO_ONLY:
      case NewsItem.ITEM_VIDEO_SPECIAL:
      case NewsItem.ITEM_WEBVIEW:
      case NewsItem.ITEM_NORMAL:
        itemViewType = VIEW_TYPE_NEWS_ITEM_ONE_IMG;
        break;
    }
    return itemViewType;
  }
}

class ViewTypeDataContainer {
  int viewType;
  dynamic data;

  ViewTypeDataContainer(this.viewType, this.data);
}
