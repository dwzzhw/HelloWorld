import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';

class FeedItemNewsView extends StatelessWidget {
  final FeedItemDetailInfo feedItemDetail;

  FeedItemNewsView(this.feedItemDetail);

  @override
  Widget build(BuildContext context) {
    return new Container(
      padding: const EdgeInsets.all(10.0),
      child: new Row(
        children: [
          new Expanded(
            child: new Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                new Container(
                  padding: const EdgeInsets.only(bottom: 8.0),
                  child: new Text(
                    '${feedItemDetail.title}',
                    style: new TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                    maxLines: 2,
                  ),
                ),
                new Text(
                  '${feedItemDetail.abstract}',
                  style: new TextStyle(
                    color: Colors.grey[500],
                  ),
                  maxLines: 1,
                ),
              ],
            ),
          ),
          new Container(
            padding: const EdgeInsets.fromLTRB(10, 0, 0, 0),
            child: new Image.network(
              feedItemDetail.imgurl,
              width: 160.0,
              height: 90.0,
              fit: BoxFit.cover,
            ),
          ),
        ],
      ),
    );
  }
}
