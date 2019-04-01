import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/image_item.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';

class NewsDetailImageView extends StatelessWidget {
  final NewsDetailItemImgContent itemContent;

  NewsDetailImageView(this.itemContent);

  @override
  Widget build(BuildContext context) {
    double fontSize = 18;
    double lineSpacing = 1.2;
    Color textColor = Colors.black;
    double itemPaddingTop = 10;
    double itemPaddingBottom = 10;

    ImageItem itemItem = itemContent.getPreviewImg();
    return Container(
      padding: EdgeInsets.fromLTRB(0, itemPaddingTop, 0, itemPaddingBottom),
      child: Image.network(itemItem.imgurl,
          width: 320, height: 180, fit: BoxFit.contain),
    );
  }
}
