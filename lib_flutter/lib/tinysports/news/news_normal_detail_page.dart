import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/sports_base_stateless_page.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';
import 'package:lib_flutter/tinysports/news/view/news_detail_image_view.dart';
import 'package:lib_flutter/tinysports/news/view/news_detail_text_view.dart';
import 'package:lib_flutter/tinysports/news/view/news_detail_video_view.dart';
import 'package:lib_flutter/utils/date_util.dart';

class NewsNormalDetailPage extends SportsBaseStatelessPage {
  final NewsDetailInfo newsDetailInfo;

  NewsNormalDetailPage(this.newsDetailInfo);

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        leading: Icon(Icons.chevron_left),
        title: new Text(''),
      ),
      body: Container(
        padding: EdgeInsets.all(12),
        child: _getNewsDetailListView(),
      ),
    );
  }

  Widget _getNewsDetailListView() {
    List<NewsDetailItemContentBase> itemList = newsDetailInfo?.content;
    if (itemList == null || itemList.length == 0) {
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

      return new ListView.builder(
          itemCount: itemList.length + headerList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getNewsDetailContentItemView(position < headerList.length
                ? headerList[position]
                : itemList[position - headerList.length]);
          });
    }
  }

  Widget _getNewsDetailContentItemView(NewsDetailItemContentBase itemContent) {
    Widget resultView;
    if (itemContent is NewsDetailItemTxtContent) {
      resultView = NewsDetailTextView(itemContent);
    } else if (itemContent is NewsDetailItemImgContent) {
      resultView = NewsDetailImageView(itemContent);
    } else if (itemContent is NewsDetailItemVideoContent) {
      resultView = NewsDetailVideoView(itemContent);
    } else {
      resultView = Text('Unsupported data');
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
        subTitle += DateUtil.getDateHourMinutePart(newsDetailInfo.pub_time);
      }
    }
    return subTitle;
  }
}
