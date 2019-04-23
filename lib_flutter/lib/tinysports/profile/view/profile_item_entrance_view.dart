import 'package:flutter/material.dart';
import '../../../tinysports/profile/data/profile_page_info.dart';

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
            width: 24,
            height: 24,
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
