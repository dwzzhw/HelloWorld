import 'package:flutter/material.dart';
import '../../http/base_data_model.dart';
import '../../tinysports/base/data/schedule_info.dart';
import '../../tinysports/base/sport_base_page_state.dart';
import '../../tinysports/base/sports_base_page.dart';
import '../../tinysports/base/view/schedule_item_view.dart';
import '../../tinysports/schedule/model/schedule_list_model.dart';
import '../../tinysports/schedule/view/list_group_title_view.dart';

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
        ScheduleListModel(fetchDataFromModel, (BaseDataModel dataModel, errCode, errMsg, int dataType) {
      onFetchDataError(errMsg);
    });
    scheduleListModel.loadData();
  }

  void fetchDataFromModel(BaseDataModel dataModel, int dataType) {
    llog('-->fetchDataFromModel(), scheduleListData=${dataModel.mRespData}');
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
    llog(
        '-->build(), targetWidget=$targetWidget, needAppBar=${widget.needAppBar}');
    return targetWidget;
  }

  Widget _getScheduleListPageContentWidget() {
    llog('-->_getScheduleListPageContentWidget(), isSuccess=$isSuccess');
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
