import 'package:flutter/material.dart';
import '../../tinysports/base/data/news_item.dart';
import '../../tinysports/base/sports_base_stateless_page.dart';
import '../../tinysports/base/view/app_bar_back_button.dart';
import '../../tinysports/base/view/common_view_manager.dart';
import '../../tinysports/news/data/news_detail_info.dart';
import '../../tinysports/news/data/news_detail_item_content.dart';
import '../../tinysports/news/data/news_detail_item_subject_content.dart';
import '../../tinysports/news/view/news_special_page_text_view.dart';
import '../../tinysports/news/view/news_special_page_top_pic_view.dart';
import '../../utils/common_utils.dart';
import '../../utils/date_util.dart';

///资讯专题页
class NewsSpecialDetailPage extends SportsBaseStatelessPage {
  final NewsDetailInfo newsDetailInfo;

  NewsSpecialDetailPage(this.newsDetailInfo);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: AppBarBackButton(),
        title: Text(newsDetailInfo?.title),
      ),
      body: Container(
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
      return ListView.builder(
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
        itemView = Container(
            padding: EdgeInsets.fromLTRB(CommonViewManager.PAGE_HORIZON_MARGIN,
                0, CommonViewManager.PAGE_HORIZON_MARGIN, 0),
            child: CommonViewManager.getNewsItemView(itemContent.data));
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
        subTitle += DateUtil.getDateMonthDayHourMinPart(newsDetailInfo.pub_time);
      }
    }
    return subTitle;
  }
}
