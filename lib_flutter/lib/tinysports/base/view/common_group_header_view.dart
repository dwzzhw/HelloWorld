import 'package:flutter/material.dart';

class CommonGroupHeaderView extends StatelessWidget {
  final String groupTitle;

  CommonGroupHeaderView(this.groupTitle);

  @override
  Widget build(BuildContext context) {
    double fontSize = 18;
    double lineSpacing = 1.2;
    Color textColor = Colors.black;
    double itemPaddingTop = 10;
    double itemPaddingBottom = 10;
    double itemPaddingLR = 12;

    return Container(
      padding: EdgeInsets.fromLTRB(
          itemPaddingLR, itemPaddingTop, itemPaddingLR, itemPaddingBottom),
      child: Text(
        groupTitle,
        style: TextStyle(
            fontSize: fontSize, color: textColor, height: lineSpacing),
      ),
    );
  }
}
