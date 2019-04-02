import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/base/sports_base_stateless_page.dart';
import 'package:lib_flutter/tinysports/feed/view/feed_item_news_view.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_subject_content.dart';
import 'package:lib_flutter/tinysports/news/view/news_special_page_text_view.dart';
import 'package:lib_flutter/tinysports/news/view/news_special_page_top_pic_view.dart';
import 'package:lib_flutter/utils/common_utils.dart';
import 'package:lib_flutter/utils/date_util.dart';

///资讯专题页
class NewsSpecialDetailPage extends SportsBaseStatelessPage {
  final NewsDetailInfo newsDetailInfo;

  NewsSpecialDetailPage(this.newsDetailInfo);

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        leading: Icon(Icons.chevron_left),
        title: new Text(''),
      ),
      body: Container(
        padding: EdgeInsets.all(12),
        child: _getSpecialListView(),
      ),
    );
  }

  Widget _getSpecialListView() {
    List<NewsDetailItemContentBase> itemList = newsDetailInfo?.content;
    if (itemList == null || itemList.length == 0) {
      return Container(
        child: Center(
          child: Text('Content is empty'),
        ),
      );
    } else {
      NewsDetailItemContentBase firstItem = itemList[0];
      NewsDetailItemSubjectContent newsSpecialInfo;
      if (firstItem is NewsDetailItemSubjectContent) {
        newsSpecialInfo = firstItem;
      }

      if (newsSpecialInfo == null || !newsSpecialInfo.hasValidData()) {
        return Container(
          child: Center(
            child: Text('Bad data format'),
          ),
        );
      }

      List<NewsSubjectSectionData> sectionData =
          newsSpecialInfo.info.sectionData;

      List<NewsSpecialListViewDataContainer> viewDataList = List();
      if (!CommonUtils.isEmptyStr(newsDetailInfo.imgurl)) {
        viewDataList.add(NewsSpecialListViewDataContainer(
            NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_PIC_HEADER,
            newsDetailInfo.imgurl));
      }
      if (!CommonUtils.isEmptyStr(newsDetailInfo.title)) {
        viewDataList.add(NewsSpecialListViewDataContainer(
            NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_TITLE,
            newsDetailInfo.title));
      }
      if (!CommonUtils.isEmptyStr(newsDetailInfo.abstract)) {
        viewDataList.add(NewsSpecialListViewDataContainer(
            NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_SUBTITLE,
            newsDetailInfo.abstract));
      }

      sectionData.forEach((NewsSubjectSectionData sectionData) {
        if (!CommonUtils.isEmptyStr(sectionData.title)) {
          viewDataList.add(NewsSpecialListViewDataContainer(
              NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_GROUP_TITLE,
              sectionData.title));

          if (!CommonUtils.isListEmpty(sectionData.artlist)) {
            sectionData.artlist.forEach((NewsItem artItem) {
              viewDataList.add(NewsSpecialListViewDataContainer(
                  NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_NORMAL_ITEM,
                  artItem));
            });
          }
        }
      });

      log('-->item count=${viewDataList.length}');
      return new ListView.builder(
          itemCount: viewDataList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getSpecialItemView(viewDataList[position]);
          });
    }
  }

  Widget _getSpecialItemView(NewsSpecialListViewDataContainer itemContent) {
    Widget itemView;
    switch (itemContent.viewType) {
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_TITLE:
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_SUBTITLE:
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_GROUP_TITLE:
        itemView =
            NewsSpecialPageTextView(itemContent.viewType, itemContent.data);
        break;
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_PIC_HEADER:
        itemView = NewsSpecialPageTopPicView(itemContent.data);
        break;
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_GROUP_TITLE:
        itemView = NewsSpecialPageTopPicView(itemContent.data);
        break;
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_NORMAL_ITEM:
        itemView = FeedItemNewsView(itemContent.data);
        break;
    }
    return itemView;
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
        subTitle += DateUtil.getDateHourMinutePart(newsDetailInfo.pub_time);
      }
    }
    return subTitle;
  }
}
