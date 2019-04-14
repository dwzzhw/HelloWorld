import 'package:json_annotation/json_annotation.dart';

part 'img_txt_ids_info.g.dart';

@JsonSerializable()
class ImgTxtIdsInfo extends Object {

  List<String> index;

  ImgTxtIdsInfo(this.index,);

  factory ImgTxtIdsInfo.fromJson(Map<String, dynamic> srcJson) => _$ImgTxtIdsInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ImgTxtIdsInfoToJson(this);

}


