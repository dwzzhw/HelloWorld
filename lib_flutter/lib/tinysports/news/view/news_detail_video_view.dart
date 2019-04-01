import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/image_item.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';
import 'package:lib_flutter/utils/Loger.dart';

class NewsDetailVideoView extends StatelessWidget {
  static const TAG = 'NewsDetailVideoView';

  final NewsDetailItemVideoContent itemContent;

  NewsDetailVideoView(this.itemContent);

  @override
  Widget build(BuildContext context) {
    double fontSize = 18;
    double lineSpacing = 1.2;
    Color textColor = Colors.black;

    double itemPaddingTop = 10;
    double itemPaddingBottom = 10;

    ImageItem itemItem = itemContent.getPreviewImg();
    double screenWidth = MediaQuery.of(context).size.width;
    logd(TAG, '-->screenWidth=$screenWidth');

    double resultWidth = screenWidth - 12 * 2;
    double resultHeight;
    double targetWidth = double.tryParse(itemItem.width);
    double targetHeight = double.tryParse(itemItem.height);
    if (targetWidth != null &&
        targetWidth > 0 &&
        targetHeight != null &&
        targetHeight > 0) {
      resultHeight = resultWidth * targetHeight / targetWidth;
    } else {
      resultHeight = resultWidth * 9 / 16;
    }

    return Container(
      padding: EdgeInsets.fromLTRB(0, itemPaddingTop, 0, itemPaddingBottom),
      child: Column(
        children: <Widget>[
          Stack(
            alignment: Alignment.center,
            children: <Widget>[
              CachedNetworkImage(
                  imageUrl: itemItem.imgurl,
                  width: resultWidth,
                  height: resultHeight,
                  fit: BoxFit.contain),
              Container(
                color: Colors.white54,
                child: Icon(
                  Icons.play_arrow,
                  size: 50,
                ),
              ),
              Positioned(
                child: Container(
                  padding: EdgeInsets.fromLTRB(5, 2, 5, 2),
                  color: Colors.black54,
                  child: Text(itemContent.duration,
                      style: TextStyle(color: Colors.white, fontSize: 10)),
                ),
                right: 12,
                bottom: 12,
              )
            ],
          ),
          Container(
            padding: EdgeInsets.all(12),
            child: Text(
              itemContent.title,
              style: TextStyle(fontSize: 12, color: Colors.grey),
            ),
          )
        ],
      ),
    );
  }
}
