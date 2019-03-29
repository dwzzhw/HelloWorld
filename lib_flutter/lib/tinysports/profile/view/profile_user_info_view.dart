import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/profile/data/profile_page_info.dart';

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
          Image.asset('lake'),
          Container(
            padding: EdgeInsets.fromLTRB(10, 0, 0, 0),
            child: Text(
              '登录可享受更多服务',
              style: TextStyle(fontSize: 16),
            ),
          )
        ],
      ),
    );
  }
}
