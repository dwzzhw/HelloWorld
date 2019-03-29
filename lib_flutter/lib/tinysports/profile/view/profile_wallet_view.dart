import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/profile/data/profile_page_info.dart';

class ProfileWalletView extends StatefulWidget {
  final List<ProfileItemEntrance> walletItemList;

  ProfileWalletView(this.walletItemList);

  @override
  State<StatefulWidget> createState() => ProfileWalletViewState(walletItemList);
}

class ProfileWalletViewState extends State<ProfileWalletView> {
  final List<ProfileItemEntrance> walletItemList;

  ProfileWalletViewState(this.walletItemList);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.fromLTRB(0, 20, 0, 20),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: getWalletItemWidgetList(),
      ),
    );
  }

  List<Widget> getWalletItemWidgetList() {
    List<Widget> itemList = List();
    walletItemList.forEach((item) {
      itemList.add(getWalletItemWidget(item));
    });
    return itemList;
  }

  Widget getWalletItemWidget(ProfileItemEntrance item) {
    return Container(
      padding: EdgeInsets.all(10),
      child: Row(
        children: <Widget>[
          Image.network(
            item.logo,
            width: 30,
            height: 30,
          ),
          Column(
            children: <Widget>[
              Text(
                item.name,
                style: TextStyle(fontSize: 16, color: Colors.black),
              ),
              Text(
                item.subName,
                style: TextStyle(fontSize: 12, color: Colors.grey),
              ),
            ],
          )
        ],
      ),
    );
  }
}
