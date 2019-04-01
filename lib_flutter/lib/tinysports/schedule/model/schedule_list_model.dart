import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/schedule/data/schedule_list_data.dart';
import 'package:lib_flutter/utils/Loger.dart';

class ScheduleListModel extends BaseDataModel<ScheduleListData> {
  ScheduleListModel(OnDataCompleteFunc<ScheduleListData> onCompleteFunction,
      OnDataErrorFunc onErrorFunc)
      : super(onCompleteFunction, onErrorFunc);

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/match/hotMatchList';
  }

  @override
  void parseDataContentObj(dataObj) {
    log('-->parseDataContentObj');
    mRespData = ScheduleListData.fromJson(dataObj);
  }

  List<dynamic> getGroupedScheduleInfoList() {
    List<dynamic> resultList;
    if (mRespData != null &&
        mRespData.dates != null &&
        mRespData.matches != null) {
      resultList = List();
      mRespData.dates.forEach((date) {
        DayGroupScheduleList dayGroupScheduleList = mRespData.matches[date];
        if (dayGroupScheduleList != null && dayGroupScheduleList.list != null) {
          resultList.add(date);
          resultList.addAll(dayGroupScheduleList.list);
        }
      });
    }
    log('-->getGroupedScheduleInfoList(), resultList length=${resultList?.length}');
    return resultList;
  }

  @override
  String getCacheKey() {
    return 'hot_schedule_list';
  }

  @override
  String getLogTAG() {
    return 'ScheduleListModel';
  }
}
