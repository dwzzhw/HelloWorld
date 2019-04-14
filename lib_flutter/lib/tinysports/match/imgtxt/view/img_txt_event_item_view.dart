import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/data/img_txt_item_detail_info.dart';

class ImgTxtEventItemView extends StatelessWidget {
  final ImgTxtItemDetail itemData;

  const ImgTxtEventItemView(this.itemData);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.fromLTRB(10, 10, 10, 0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: <Widget>[
          Container(
            padding: EdgeInsets.fromLTRB(0, 0, 10, 0),
            child: Text(
              itemData?.time,
              style: TextStyle(fontSize: 16, color: Colors.black),
            ),
          ),
          Expanded(
            child: Container(
              padding: EdgeInsets.fromLTRB(10, 0, 0, 0),
              child: Text(
                itemData?.content,
                maxLines: 10,
                style: TextStyle(fontSize: 16, color: Colors.black),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
