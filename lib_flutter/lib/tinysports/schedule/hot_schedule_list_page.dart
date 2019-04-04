import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/schedule_info.dart';
import 'package:lib_flutter/tinysports/base/sport_base_page_state.dart';
import 'package:lib_flutter/tinysports/base/sports_base_page.dart';
import 'package:lib_flutter/tinysports/schedule/data/schedule_list_data.dart';
import 'package:lib_flutter/tinysports/schedule/model/schedule_list_model.dart';
import 'package:lib_flutter/tinysports/schedule/view/list_group_title_view.dart';
import 'package:lib_flutter/tinysports/base/view/schedule_item_view.dart';

class HotScheduleListPage extends SportsBasePage {
  final bool needAppBar;

  HotScheduleListPage({this.needAppBar = false});

  @override
  State<StatefulWidget> createState() => HotScheduleListPageState();
}

class HotScheduleListPageState
    extends SportsBasePageState<HotScheduleListPage> {
  List<dynamic> groupedScheduleList = [];
  ScheduleListModel scheduleListModel;

  @override
  void initState() {
    super.initState();
    scheduleListModel =
        ScheduleListModel(fetchDataFromModel, (errCode, errMsg) {
      onFetchDataError(errMsg);
    });
    scheduleListModel.loadData();
  }

  void fetchDataFromModel(ScheduleListData scheduleListData) {
    log('-->fetchDataFromModel(), scheduleListData=$scheduleListData');
    setState(() {
      groupedScheduleList.clear();
      groupedScheduleList
          .addAll(scheduleListModel.getGroupedScheduleInfoList());
    });
  }

  @override
  Widget build(BuildContext context) {
    Widget targetWidget;
    if (widget.needAppBar) {
      targetWidget = new Scaffold(
        appBar: new AppBar(
          title: new Text('Hot Schedule List'),
        ),
        body: _getScheduleListPageContentWidget(),
      );
    } else {
      targetWidget = _getScheduleListPageContentWidget();
    }
    log('-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getScheduleListPageContentWidget() {
    log('-->_getScheduleListPageContentWidget(), isSuccess=$isSuccess');
    if (!isSuccess) {
      String tipsMsg = errTipsMsg;
      if (tipsMsg == null || tipsMsg.length == 0) {
        tipsMsg = 'fail to fetch data from net.';
      }
      return new Center(
        child: Text('$tipsMsg'),
      );
    } else if (groupedScheduleList.length > 0) {
      return new ListView.builder(
          itemCount: groupedScheduleList.length,
          itemBuilder: (BuildContext context, int position) {
            return _getScheduleItemWidget(position);
          });
    } else {
      return new Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  Widget _getScheduleItemWidget(int itemIndex) {
    dynamic itemData = groupedScheduleList[itemIndex];
    if (itemData is ScheduleInfo) {
      return ScheduleItemView(itemData);
    } else {
      return ListGroupTitleView(itemData);
    }
  }

  @override
  String getLogTAG() {
    return 'HotScheduleListPage';
  }
}
