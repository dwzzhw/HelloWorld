import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/schedule_info.dart';

part 'schedule_list_data.g.dart';

@JsonSerializable()
class ScheduleListData extends Object {

  Map<String, DayGroupScheduleList> matches;

  String latestMatchDate;

  List<String> dates;

  String today;

  String updateFrequency;

  List<String> updateDates;

  ScheduleListData(this.matches,this.latestMatchDate,this.dates,this.today,this.updateFrequency,this.updateDates,);

  factory ScheduleListData.fromJson(Map<String, dynamic> srcJson) => _$ScheduleListDataFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ScheduleListDataToJson(this);

}

@JsonSerializable()
class DayGroupScheduleList extends Object {

List<ScheduleInfo> list;

String version;

DayGroupScheduleList(this.list,this.version,);

factory DayGroupScheduleList.fromJson(Map<String, dynamic> srcJson) => _$DayGroupScheduleListFromJson(srcJson);

Map<String, dynamic> toJson() => _$DayGroupScheduleListToJson(this);

}