import 'package:flutter/material.dart';
import '../../../tinysports/news/data/news_detail_item_subject_content.dart';

class NewsSpecialPageTextView extends StatelessWidget {
  final int viewType;
  final String txtStr;

  NewsSpecialPageTextView(this.viewType, this.txtStr);

  @override
  Widget build(BuildContext context) {
    double fontSize = 18;
    double lineSpacing = 1.2;
    Color textColor = Colors.black;
    double itemPaddingTop = 10;
    double itemPaddingBottom = 10;
    double itemPaddingLR = 12;

    switch (viewType) {
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_TITLE:
        fontSize = 24;
        lineSpacing = 1.1;
        itemPaddingTop = 0;
        break;
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_SUBTITLE:
        fontSize = 12;
        itemPaddingTop = 0;
        textColor = Colors.grey;
        break;
      case NewsSpecialListViewDataContainer.VIEW_TYPE_PAGE_GROUP_TITLE:
        fontSize = 18;
        break;
    }
    return Container(
      padding: EdgeInsets.fromLTRB(itemPaddingLR, itemPaddingTop, itemPaddingLR, itemPaddingBottom),
      child: Text(
        txtStr,
        style: TextStyle(
            fontSize: fontSize, color: textColor, height: lineSpacing),
      ),
    );
  }
}
