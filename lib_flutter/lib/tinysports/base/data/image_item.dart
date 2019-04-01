import 'package:json_annotation/json_annotation.dart';

part 'image_item.g.dart';

@JsonSerializable()
class ImageItem extends Object {
  String height;

  String imgurl;

  String width;

  ImageItem(
    this.height,
    this.imgurl,
    this.width,
  );

  bool isValid() {
    return imgurl != null && imgurl.length > 0;
  }

  factory ImageItem.fromJson(Map<String, dynamic> srcJson) =>
      _$ImageItemFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ImageItemToJson(this);
}
