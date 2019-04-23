import 'package:flutter/material.dart';
import '../../../tinysports/profile/data/profile_page_info.dart';

class ProfileUserInfoView extends StatefulWidget {
  final UserInfo userInfo;

  const ProfileUserInfoView(this.userInfo);

  @override
  State<StatefulWidget> createState() => ProfileUserInfoViewState(userInfo);
}

class ProfileUserInfoViewState extends State<ProfileUserInfoView> {
  final UserInfo userInfo;

  ProfileUserInfoViewState(this.userInfo);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Row(
        children: <Widget>[
          Container(
            padding: EdgeInsets.fromLTRB(20, 20, 20, 20),
            child: Image.asset(
              'assets/images/lake.jpg',
              width: 60.0,
              height: 60.0,
              fit: BoxFit.cover,
            ),
          ),
          Container(
            padding: EdgeInsets.fromLTRB(10, 0, 0, 0),
            child: Text(
              '登录可享受更多服务',
              style: TextStyle(fontSize: 18),
            ),
          )
        ],
      ),
    );
  }
}
