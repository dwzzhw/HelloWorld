import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/utils/Loger.dart';

typedef void OnNavigatorItemClicked(int itemType, bool isSelected);

class MainNavigatorItemView extends StatelessWidget {
  static const String TAG = 'MainNavigatorItemView';
  static const int ITEM_TYPE_HOME = 1;
  static const int ITEM_TYPE_SCHEDULE = 2;
  static const int ITEM_TYPE_BBS = 3;
  static const int ITEM_TYPE_PROFILE = 4;

  final bool isSelected;
  final int itemType;
  final OnNavigatorItemClicked onClickListener;

  MainNavigatorItemView(this.isSelected, this.itemType, this.onClickListener);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        logd(TAG,
            '-->navigator item is clicked, type=$itemType, isSelected=$isSelected');
        if (onClickListener != null) {
          onClickListener(itemType, isSelected);
        }
      },
      child: Container(
        padding: EdgeInsets.all(10),
        child: Column(
          children: <Widget>[
            getIconWidget(),
            getTitleWidget(),
          ],
        ),
      ),
    );
  }

  Widget getIconWidget() {
    return Image.asset(
      getItemLogoPath(),
      width: 30.0,
      height: 30.0,
      fit: BoxFit.fill,
    );
  }

  Widget getTitleWidget() {
    return Text(
      getItemTitle(),
      style: TextStyle(fontSize: 12, color: getTitleColor()),
    );
  }

  String getItemLogoPath() {
    String logoSrc;
    switch (itemType) {
      case ITEM_TYPE_HOME:
        logoSrc = isSelected
            ? 'assets/images/tab_news_current.png'
            : 'assets/images/tab_news.png';
        break;
      case ITEM_TYPE_SCHEDULE:
        logoSrc = isSelected
            ? 'assets/images/tab_match_current.png'
            : 'assets/images/tab_match.png';
        break;
      case ITEM_TYPE_BBS:
        logoSrc = isSelected
            ? 'assets/images/tab_shequ_current.png'
            : 'assets/images/tab_shequ.png';
        break;
      case ITEM_TYPE_PROFILE:
        logoSrc = isSelected
            ? 'assets/images/tab_me_current.png'
            : 'assets/images/tab_me.png';
        break;
    }
    return logoSrc;
  }

  String getItemTitle() {
    String itemTitle;
    switch (itemType) {
      case ITEM_TYPE_HOME:
        itemTitle = '首页';
        break;
      case ITEM_TYPE_SCHEDULE:
        itemTitle = '赛程';
        break;
      case ITEM_TYPE_BBS:
        itemTitle = '社区';
        break;
      case ITEM_TYPE_PROFILE:
        itemTitle = '我的';
        break;
    }
    return itemTitle;
  }

  Color getTitleColor() {
    return isSelected ? Colors.blue : Colors.grey;
  }
}
