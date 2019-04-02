import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';

class NewsSpecialPageTopPicView extends StatelessWidget {
  final String urlStr;

  NewsSpecialPageTopPicView(this.urlStr);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.fromLTRB(0, 0, 0, 0),
      child: CachedNetworkImage(
        imageUrl: urlStr,
        fit: BoxFit.fill,
      ),
    );
  }
}
