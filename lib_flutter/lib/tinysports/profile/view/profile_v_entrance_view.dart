import 'package:flutter/material.dart';
import '../../../tinysports/profile/data/profile_page_info.dart';
import '../../../tinysports/profile/view/profile_item_entrance_view.dart';
import '../../../utils/Loger.dart';

class ProfileVEntranceView extends StatelessWidget {
  final String TAG = 'ProfileVEntranceView';
  final List<ProfileItemEntrance> itemList;

  ProfileVEntranceView(this.itemList);

  @override
  Widget build(BuildContext context) {
    int itemCnt = itemList.length;
    int lineCnt = itemCnt ~/ 4 + (itemCnt % 4 == 0 ? 0 : 1);
    List<Widget> lineWidgetList = List();
    llogd(TAG, '-->build, itemCnt=$itemCnt, lineCnt=$lineCnt');
    for (int i = 0; i < lineCnt; i++) {
      List<Widget> columnWidgetList = List();
      for (int j = i * 4; j < (i + 1) * 4; j++) {
        if (j < itemCnt) {
          columnWidgetList.add(ProfileItemEntranceView(itemList[j]));
        }
//        else {
//          columnWidgetList.add(Container());
//        }
      }
      lineWidgetList.add(Row(
        children: columnWidgetList,
//        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      ));
    }
    return Container(
      child: Column(
        children: lineWidgetList,
      ),
    );

//    return Container(
//      child: GridView.builder(
//        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
//          crossAxisCount: 4,
//        ),
//        itemCount: itemList.length,
//        itemBuilder: (context, index) {
//          return ProfileItemEntranceView(itemList[index]);
//        },
//      ),
//      height: lineCnt * 110.0,
//    );
  }
}
