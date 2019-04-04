import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/comment_item.dart';
import 'package:lib_flutter/utils/date_util.dart';

class CommentItemView extends StatelessWidget {
  final CommentItem commentItem;

  CommentItemView(this.commentItem);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.fromLTRB(0, 0, 0, 20),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Container(
            padding: EdgeInsets.fromLTRB(0, 0, 12, 0),
            child: getUserIconView(),
          ),
          Expanded(
            child: getColumnWidget(),
          ),
        ],
      ),
    );
  }

  Widget getUserIconView() {
    double iconSize = 40;
    if (commentItem.userinfo != null) {
      return CachedNetworkImage(
        imageUrl: commentItem.userinfo.head,
        width: iconSize,
        height: iconSize,
      );
    } else {
      return Container(
        width: iconSize,
        height: iconSize,
      );
    }
  }

  Widget getColumnWidget() {
    String userName =
        commentItem.userinfo != null ? commentItem.userinfo.nick : '';
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: <Widget>[
        Text(
          userName,
          style: TextStyle(
              fontSize: 16, color: Colors.black, fontWeight: FontWeight.w700),
        ),
        Container(
          padding: EdgeInsets.fromLTRB(0, 2, 0, 6),
          child: Text(
            getDateTimeStr(),
            style: TextStyle(fontSize: 12),
          ),
        ),
        Container(
          child: Text(
            commentItem.content,
            style: TextStyle(fontSize: 18),
          ),
        )
      ],
    );
  }

  String getDateTimeStr() {
    return DateUtil.getFormattedStrFromDateObj(
        DateTime.fromMillisecondsSinceEpoch(int.tryParse(commentItem.time)),
        DateUtil.FORMAT_STR_MONTH_DAY_HOUR_MINUTE);
  }
}
