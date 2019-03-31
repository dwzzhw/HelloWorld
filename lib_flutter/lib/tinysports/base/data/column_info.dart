import 'package:json_annotation/json_annotation.dart';

part 'column_info.g.dart';

@JsonSerializable()
class ColumnInfo extends Object {
  String name;

  String icon;

  String columnId;

  String rankColumn;

  String type;

  ColumnInfo(
    this.name,
    this.icon,
    this.columnId,
    this.rankColumn,
    this.type,
  );

  ColumnInfo.localColumnInfo(String name, String columnId) {
    this.name = name;
    this.columnId = columnId;
  }

  factory ColumnInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$ColumnInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ColumnInfoToJson(this);
}

@JsonSerializable()
class ColumnInfoList extends Object {
  List<ColumnInfo> list;

  String columnVersion;

  List<dynamic> first;

  ColumnInfoList(
    this.list,
    this.columnVersion,
    this.first,
  );

  factory ColumnInfoList.fromJson(Map<String, dynamic> srcJson) =>
      _$ColumnInfoListFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ColumnInfoListToJson(this);
}
