import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';

class FeedItemNewsView extends StatelessWidget {
  final NewsItem feedItemDetail;

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
            child: CachedNetworkImage(
              imageUrl: feedItemDetail.imgurl,
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
