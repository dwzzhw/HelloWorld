import 'package:flutter/material.dart';
import '../../http/base_data_model.dart';
import '../../tinysports/base/sport_base_page_state.dart';
import '../../tinysports/base/sports_base_page.dart';
import '../../tinysports/profile/data/profile_page_info.dart';
import '../../tinysports/profile/model/profile_page_model.dart';
import '../../tinysports/profile/view/profile_k_entrance_view.dart';
import '../../tinysports/profile/view/profile_user_info_view.dart';
import '../../tinysports/profile/view/profile_v_entrance_view.dart';
import '../../tinysports/profile/view/profile_wallet_view.dart';

class ProfilePage extends SportsBasePage {
  final bool needAppBar;

  ProfilePage({this.needAppBar = false});

  @override
  State<StatefulWidget> createState() => ProfilePageState();
}

class ProfilePageState extends SportsBasePageState<ProfilePage> {
  static const String VIEW_TYPE_USER_INFO = 'user_info';
  static const String VIEW_TYPE_WALLET_ENTRANCE = 'wallet_entrance';
  static const String VIEW_TYPE_V_ENTRANCE = 'v_entrance';
  static const String VIEW_TYPE_K_ENTRANCE = 'k_entrance';

  List<String> profilePageItemList = [];
  ProfilePageModel profilePageModel;
  ProfilePageInfo profilePageInfo;

  @override
  void initState() {
    super.initState();
    profilePageModel =
        ProfilePageModel(fetchDataFromModel, (BaseDataModel dataModel, errCode, errMsg, int dataType) {
      onFetchDataError(errMsg);
    });
    profilePageModel.loadData();
  }

  void fetchDataFromModel(BaseDataModel dataModel, int dataType) {
    llog('-->fetchDataFromModel(), profilePageInfo=${dataModel.mRespData}');
    setState(() {
      this.profilePageInfo = dataModel.mRespData;
      profilePageItemList.clear();
      if (profilePageInfo != null) {
        if (profilePageInfo.userInfo != null) {
          profilePageItemList.add(VIEW_TYPE_USER_INFO);
        }
        if (profilePageInfo.walletEntrance != null &&
            profilePageInfo.walletEntrance.length > 0) {
          profilePageItemList.add(VIEW_TYPE_WALLET_ENTRANCE);
        }
        if (profilePageInfo.vEntrance != null &&
            profilePageInfo.vEntrance.length > 0) {
          profilePageItemList.add(VIEW_TYPE_V_ENTRANCE);
        }
        if (profilePageInfo.kEntrance != null &&
            profilePageInfo.kEntrance.length > 0) {
          profilePageItemList.add(VIEW_TYPE_K_ENTRANCE);
        }
      } else {
        onFetchDataError('Profile page is empty');
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    Widget targetWidget;
    if (widget.needAppBar) {
      targetWidget = new Scaffold(
        appBar: new AppBar(
          title: new Text('Profile page info'),
        ),
        body: _getProfilePageContentWidget(),
      );
    } else {
      targetWidget = _getProfilePageContentWidget();
    }
    llog(
        '-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getProfilePageContentWidget() {
    llog('-->_getProfilePageContentWidget(), isSuccess=$isSuccess');
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      return new Center(
        child: Text('$tipsMsg'),
      );
    } else if (profilePageItemList.length > 0) {
      return new ListView.builder(
          itemCount: profilePageItemList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getProfileGroupItemWidget(position);
          });
    } else {
      return new Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  Widget _getProfileGroupItemWidget(int itemIndex) {
    llog('-->_getProfileGroupItemWidget(), itemIndex=$itemIndex');
    Widget itemWidget;
    String itemType = profilePageItemList[itemIndex];
    if (itemType == VIEW_TYPE_USER_INFO) {
      itemWidget = ProfileUserInfoView(profilePageInfo.userInfo);
    } else if (itemType == VIEW_TYPE_WALLET_ENTRANCE) {
      itemWidget = ProfileWalletView(profilePageInfo.walletEntrance);
    } else if (itemType == VIEW_TYPE_V_ENTRANCE) {
      itemWidget = ProfileVEntranceView(profilePageInfo.vEntrance);
    } else if (itemType == VIEW_TYPE_K_ENTRANCE) {
      itemWidget = ProfileKEntranceView(profilePageInfo.kEntrance[0]);
    }
    if (itemWidget == null) {
      itemWidget = Text('Item of $itemIndex');
    }
    return itemWidget;
  }
}
