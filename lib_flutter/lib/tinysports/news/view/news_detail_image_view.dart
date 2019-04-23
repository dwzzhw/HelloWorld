import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import '../../../tinysports/base/data/image_item.dart';
import '../../../tinysports/news/data/news_detail_item_content.dart';
import '../../../utils/Loger.dart';

class NewsDetailImageView extends StatelessWidget {
  static const TAG = 'NewsDetailImageView';

  final NewsDetailItemImgContent itemContent;

  NewsDetailImageView(this.itemContent);

  @override
  Widget build(BuildContext context) {
    double itemPaddingTop = 10;
    double itemPaddingBottom = 10;

    ImageItem itemItem = itemContent.getPreviewImg();
    double screenWidth = MediaQuery.of(context).size.width;
    llogd(TAG, '-->screenWidth=$screenWidth');

    double resultWidth = screenWidth - 12 * 2;
    double resultHeight;
    double targetWidth = double.tryParse(itemItem.width);
    double targetHeight = double.tryParse(itemItem.height);
    if (targetWidth != null &&
        targetWidth > 0 &&
        targetHeight != null &&
        targetHeight > 0) {
      resultHeight = resultWidth * targetHeight / targetWidth;
    } else {
      resultHeight = resultWidth * 9 / 16;
    }

    return Container(
      padding: EdgeInsets.fromLTRB(0, itemPaddingTop, 0, itemPaddingBottom),
      child: CachedNetworkImage(
          imageUrl: itemItem.imgurl,
          width: resultWidth,
          height: resultHeight,
          fit: BoxFit.contain),
    );
  }
}
