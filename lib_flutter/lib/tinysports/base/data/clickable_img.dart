import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/jump_data.dart';

part 'clickable_img.g.dart';

@JsonSerializable()
class ClickableImage extends Object {
  String icon;

  JumpData jumpData;

  ClickableImage(
    this.icon,
    this.jumpData,
  );

  factory ClickableImage.fromJson(Map<String, dynamic> srcJson) =>
      _$ClickableImageFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ClickableImageToJson(this);
}
