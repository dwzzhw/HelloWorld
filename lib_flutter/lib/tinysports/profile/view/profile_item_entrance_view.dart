import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/profile/data/profile_page_info.dart';

class ProfileItemEntranceView extends StatelessWidget {
  final ProfileItemEntrance item;

  ProfileItemEntranceView(this.item);

  @override
  Widget build(BuildContext context) {
    return Container(width: 98, height: 80,
      child: Column(
        children: <Widget>[
          Image.network(
            item.logo,
            width: 30,
            height: 30,
          ),
          Text(
            item.name,
            style: TextStyle(fontSize: 16),
          )
        ],
      ),
    );
  }
}
