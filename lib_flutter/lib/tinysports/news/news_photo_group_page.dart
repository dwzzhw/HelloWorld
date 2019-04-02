import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/image_item.dart';
import 'package:lib_flutter/tinysports/base/sports_base_stateless_page.dart';
import 'package:lib_flutter/tinysports/base/view/app_bar_back_button.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';

class NewsPhotoGroupPage extends SportsBaseStatelessPage {
  final NewsDetailInfo newsDetailInfo;

  NewsPhotoGroupPage(this.newsDetailInfo);

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        leading: AppBarBackButton(),
        backgroundColor: Colors.black,
        title: new Text(
          newsDetailInfo.title,
          maxLines: 1,
        ),
      ),
      body: Container(
        padding: EdgeInsets.all(0),
        child: _getPhotoGroupView(),
      ),
    );
  }

  Widget _getPhotoGroupView() {
    List<NewsDetailItemContentBase> itemList = newsDetailInfo?.content;
    if (itemList == null || itemList.length == 0) {
      return Container(
        child: Center(
          child: Text('Content is empty'),
        ),
      );
    } else {
      List<NewsDetailItemImgContent> imageItemList = List();
      List<NewsDetailItemTxtContent> descItemList = List();
      NewsDetailItemContentBase lastItem;
      itemList.forEach((itemInfo) {
        if (itemInfo is NewsDetailItemImgContent) {
          if (lastItem is NewsDetailItemImgContent) {
            //连续两张图
            descItemList.add(NewsDetailItemTxtContent(
                NewsDetailItemContentBase.STR_TEXT, ''));
          }
          imageItemList.add(itemInfo);
        } else if (itemInfo is NewsDetailItemTxtContent) {
          if (lastItem is NewsDetailItemImgContent) {
            descItemList.add(itemInfo);
          } //连续两条描述的场景忽略
        }
        lastItem = itemInfo;
      });

      log('-->_getPhotoGroupView(), img cnt=${imageItemList?.length}, desc cnt=${descItemList?.length}');

      List<Widget> pageList = List();
      for (int i = 0; i < imageItemList.length; i++) {
        NewsDetailItemImgContent imgItem = imageItemList[i];
        NewsDetailItemTxtContent txtItem =
            i < descItemList.length ? descItemList[i] : null;
        String descStr = '(${i + 1}/${imageItemList.length})';
        if (txtItem != null) {
          descStr += txtItem.info;
        }
        pageList.add(_getPageViewItem(imgItem, descStr));
      }
      return PageView(
        children: pageList,
      );
    }
  }

  Widget _getPageViewItem(NewsDetailItemImgContent imgContent, String descStr) {
    ImageItem img = imgContent?.getHDImg();
    if (img != null) {
      return Container(
        color: Colors.black,
        child: Stack(
          children: <Widget>[
            Center(
              child: CachedNetworkImage(
                imageUrl: img.imgurl,
              ),
            ),
            Positioned(
              bottom: 10,
              left: 0,
              right: 0,
              child: Container(
                padding: EdgeInsets.fromLTRB(12, 0, 12, 0),
                child: Text(
                  descStr,
                  style: TextStyle(fontSize: 14, color: Colors.white),
                ),
              ),
            )
          ],
        ),
      );
    } else {
      return Container(
        child: Center(
          child: Text('Bad Img'),
        ),
      );
    }
  }

  @override
  String getLogTAG() {
    return 'NewsPhotoGroupPage';
  }
}
