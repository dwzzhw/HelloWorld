import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/comment_item.dart';

part 'comment_list_content_info.g.dart';

@JsonSerializable()
class CommentListContentInfo extends Object {
  Map<String, CommentItem> common;

  List<String> commentIds;

  String newNum;

  CommentListContentInfo(
    this.common,
    this.commentIds,
    this.newNum,
  );

  factory CommentListContentInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$CommentListContentInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$CommentListContentInfoToJson(this);
}
