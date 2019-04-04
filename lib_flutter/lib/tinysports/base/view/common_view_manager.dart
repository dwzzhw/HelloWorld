import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/data/schedule_info.dart';
import 'package:lib_flutter/tinysports/base/view/common_group_header_view.dart';
import 'package:lib_flutter/tinysports/base/view/news_list_item_view.dart';
import 'package:lib_flutter/tinysports/base/view/schedule_item_view.dart';

class CommonViewManager {
  static const int VIEW_TYPE_COMMON_NONE = 10000;
  static const int VIEW_TYPE_COMMON_GROUP_HEADER = 10001;

  static const int VIEW_TYPE_NEWS_ITEM_NONE_IMG = 10101;
  static const int VIEW_TYPE_NEWS_ITEM_ONE_IMG = 10102;
  static const int VIEW_TYPE_NEWS_ITEM_THREE_IMG = 10103;

  static const int VIEW_TYPE_SCHEDULE_VS = 10111;
  static const int VIEW_TYPE_SCHEDULE_NON_VS = 10112;

  static Widget getCommonView(int viewType, dynamic dataObj) {
    Widget resultView;
    switch (viewType) {
      case VIEW_TYPE_COMMON_NONE:
        resultView = getNoneView();
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
    }
    return resultView;
  }

  static Widget getNoneView() {
    return Container(
//      width: 0,
//      height: 0,
      child: Text('None view'),
    );
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
      case NewsItem.ITEM_VIDEO_SPECIAL:
      case NewsItem.ITEM_WEBVIEW:
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
