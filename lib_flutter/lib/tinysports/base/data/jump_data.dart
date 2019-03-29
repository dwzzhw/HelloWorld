import 'package:json_annotation/json_annotation.dart';

part 'jump_data.g.dart';
@JsonSerializable()
class JumpData extends Object {

  String type;

  JumpParam param;

  JumpData(this.type,this.param,);

  factory JumpData.fromJson(Map<String, dynamic> srcJson) => _$JumpDataFromJson(srcJson);

  Map<String, dynamic> toJson() => _$JumpDataToJson(this);

}


@JsonSerializable()
class JumpParam extends Object {

  String title;

  String url;

  JumpParam(this.title,this.url,);

  factory JumpParam.fromJson(Map<String, dynamic> srcJson) => _$JumpParamFromJson(srcJson);

  Map<String, dynamic> toJson() => _$JumpParamToJson(this);

}