import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';

class NewsDetailTextView extends StatelessWidget {
  final NewsDetailItemTxtContent itemContent;

  NewsDetailTextView(this.itemContent);

  @override
  Widget build(BuildContext context) {
    double fontSize = 18;
    double lineSpacing = 1.2;
    Color textColor = Colors.black;
    double itemPaddingTop = 10;
    double itemPaddingBottom = 10;

    switch (itemContent.localStyle) {
      case NewsDetailItemTxtContent.LOCAL_STYLE_TITLE:
        fontSize = 24;
        lineSpacing = 1.1;
        itemPaddingTop = 0;
        break;
      case NewsDetailItemTxtContent.LOCAL_STYLE_SUB_TITLE:
        fontSize = 12;
        itemPaddingTop = 0;
        textColor = Colors.grey;
        break;
    }
    return Container(
      padding: EdgeInsets.fromLTRB(0, itemPaddingTop, 0, itemPaddingBottom),
      child: Text(
        itemContent.info,
        style: TextStyle(
            fontSize: fontSize, color: textColor, height: lineSpacing),
      ),
    );
  }
}
