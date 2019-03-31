import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/main/view/main_navigator_item_view.dart';

class MainNavigatorBar extends StatelessWidget {
  final List<int> itemTypeList;
  final int selectedItemType;
  final OnNavigatorItemClicked onItemClickListener;

  MainNavigatorBar(
      this.itemTypeList, this.selectedItemType, this.onItemClickListener);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Container(
          height: 1,
          color: Colors.grey,
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: getItemWidgetList(),
        )
      ],
    );
  }

  List<MainNavigatorItemView> getItemWidgetList() {
    List<MainNavigatorItemView> itemWidgetList = List();
    itemTypeList.forEach((int type) {
      itemWidgetList.add(MainNavigatorItemView(
          type == selectedItemType, type, onItemClickListener));
    });
    return itemWidgetList;
  }
}
