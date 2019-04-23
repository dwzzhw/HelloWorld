import 'package:flutter/material.dart';

import '../../../utils/Loger.dart';
import '../data/profile_page_info.dart';
import 'profile_item_entrance_view.dart';

class ProfileKEntranceView extends StatelessWidget {
  final String TAG = 'ProfileKEntranceView';
  final KEntranceGroup kEntranceGroup;

  ProfileKEntranceView(this.kEntranceGroup);

  @override
  Widget build(BuildContext context) {
    int itemCnt = kEntranceGroup.list.length;
    int lineCnt = itemCnt ~/ 4 + (itemCnt % 4 == 0 ? 0 : 1);
    List<Widget> lineWidgetList = List();
    lineWidgetList.add(Row(
      children: <Widget>[
        Container(
          padding: EdgeInsets.fromLTRB(20, 0, 20, 20),
          child: Text(
            kEntranceGroup.title,
            textAlign: TextAlign.left,
            style: TextStyle(fontSize: 18),
          ),
        )
      ],
    ));
    llogd(TAG, '-->build, itemCnt=$itemCnt, lineCnt=$lineCnt');
    for (int i = 0; i < lineCnt; i++) {
      List<Widget> columnWidgetList = List();
      for (int j = i * 4; j < (i + 1) * 4; j++) {
        if (j < itemCnt) {
          columnWidgetList.add(ProfileItemEntranceView(kEntranceGroup.list[j]));
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

//    return GridView.builder(
//      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
//        crossAxisCount: 4,
//      ),
//      itemCount: kEntranceGroup.list.length,
//      itemBuilder: (context, index) {
//        return Image.network(kEntranceGroup.list[index].logo);
//      },
//    );

    return Container(
      child: Column(
        children: lineWidgetList,
      ),
    );
  }
}
