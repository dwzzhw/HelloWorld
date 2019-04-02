import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/utils/common_utils.dart';

class NewsListItemView extends StatelessWidget {
  static const String TAG = 'NewsListItemView';
  final NewsItem newsItemInfo;

  NewsListItemView(this.newsItemInfo);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        logd(TAG, 'News Item is clicked');
      },
      child: Container(
        padding: const EdgeInsets.all(10.0),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    padding: const EdgeInsets.only(bottom: 8.0),
                    child: Text(
                      '${newsItemInfo.title}',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                      ),
                      maxLines: 2,
                    ),
                  ),
                  Row(
                    children: getSubtitleList(),
                  ),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.fromLTRB(10, 0, 0, 0),
              child: CachedNetworkImage(
                imageUrl: newsItemInfo.imgurl,
                width: 160.0,
                height: 90.0,
                fit: BoxFit.cover,
              ),
            ),
          ],
        ),
      ),
    );
  }

  List<Widget> getSubtitleList() {
    List<Widget> subTitleList = List();
    String typeDesc = newsItemInfo.getNewTypeDescStr();
    if (!CommonUtils.isEmptyStr(typeDesc)) {
      subTitleList.add(Text(
        typeDesc,
        style: TextStyle(
          color: Colors.blue,
        ),
      ));
      subTitleList.add(Container(
        padding: EdgeInsets.fromLTRB(12, 0, 12, 0),
        child: Container(
          width: 1,
          height: 12,
          color: Colors.grey,
        ),
      ));
    }
    subTitleList.add(Text(
      newsItemInfo.source,
      style: TextStyle(
        color: Colors.grey[500],
      ),
      maxLines: 1,
    ));

    if (!CommonUtils.isEmptyStr(newsItemInfo.commentsNum)) {
      subTitleList.add(Container(
        padding: EdgeInsets.fromLTRB(12, 0, 12, 0),
        child: Text(
          '${newsItemInfo.commentsNum}评',
          style: TextStyle(
            color: Colors.grey[500],
          ),
        ),
      ));
    }

    return subTitleList;
  }
}
