import 'package:flutter/material.dart';
import '../../../../tinysports/match/imgtxt/data/img_txt_item_detail_info.dart';
import '../../../../utils/common_utils.dart';
import '../../../../utils/date_util.dart';

class ImgTxtCommentatorItemView extends StatelessWidget {
  final ImgTxtItemDetail itemData;

  const ImgTxtCommentatorItemView(this.itemData);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.fromLTRB(10, 20, 0, 0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Text(
            getCommentatorName(),
            style: TextStyle(fontSize: 14, color: Colors.grey[700]),
          ),
          Container(
            padding: EdgeInsets.fromLTRB(0, 6, 0, 0),
            child: Text(
              itemData?.content,
              style: TextStyle(fontSize: 16, color: Colors.black),
            ),
          )
        ],
      ),
    );
  }

  String getCommentatorName() {
    String name;
    Commentator commentator = itemData?.commentator;
    if (commentator != null) {
      name = commentator.nick;
      if (!CommonUtils.isEmptyStr(commentator.role)) {
        name = name + '(' + commentator.role + ')';
      }
      String timeLabel = DateUtil.getDateHourMinutePart(itemData.sendTime);
      if (!CommonUtils.isEmptyStr(timeLabel)) {
        name = name + '  ' + timeLabel;
      }
    }
    return name;
  }
}
