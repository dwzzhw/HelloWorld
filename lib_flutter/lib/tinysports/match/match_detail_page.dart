import 'dart:developer';

import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/view/app_bar_back_button.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_info.dart';
import 'package:lib_flutter/tinysports/match/model/match_detail_info_model.dart';
import 'package:lib_flutter/tinysports/match/view/match_detail_img_txt_header_view.dart';

class MatchDetailPage extends SportsBasePage {
  final String mid;

  MatchDetailPage(this.mid);

  @override
  State<StatefulWidget> createState() => MatchDetailPageState(mid);

//https://app.sports.qq.com/match/detail?&mid=100000:7155
//https://app.sports.qq.com/comment?&reqnum=20&pageflag=0&targetId=2974411487
//https://app.sports.qq.com/match/detailRelatedInfo?needMatchDetail=0&mid=100000:7155

}

class MatchDetailPageState extends SportsBasePageState<MatchDetailPage> {
  String mid;
  MatchDetailInfoModel matchDetailInfoModel;
  MatchDetailInfo matchDetailInfo;

  MatchDetailPageState(this.mid);

  @override
  void initState() {
    super.initState();

    matchDetailInfoModel = MatchDetailInfoModel(mid, fetchDataFromModel,
        (int code, String errMsg) {
      onFetchDataError(errMsg);
    });
    matchDetailInfoModel.loadData();
  }

  void fetchDataFromModel(MatchDetailInfo info) {
    log('-->fetchDataFromModel(), matchDetailInfo=$info');
    setState(() {
      matchDetailInfo = info;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: AppBarBackButton(),
        title: Text('Match Detail Page'),
      ),
      body: MatchDetailImgTxtHeaderView(matchDetailInfo),
    );
  }

  Widget _getMatchDetailPageContentWidget() {
    return Center(
      child: Text(matchDetailInfo != null
          ? matchDetailInfo.matchInfo.matchDesc
          : 'Match Detail Info'),
    );
  }

  @override
  String getLogTAG() {
    return 'MatchDetailPage';
  }
}
