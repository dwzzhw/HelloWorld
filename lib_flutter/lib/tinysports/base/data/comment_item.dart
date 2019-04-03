import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/user_info.dart';

part 'comment_item.g.dart';

@JsonSerializable()
class CommentItem extends Object {
  String id;

  String ishost;

  String parent;

  String replyuser;

  String time;

  String content;

  String up;

  String standSelf;

  String standHost;

  Userinfo userinfo;

  CommentItem(
    this.id,
    this.ishost,
    this.parent,
    this.replyuser,
    this.time,
    this.content,
    this.up,
    this.standSelf,
    this.standHost,
    this.userinfo,
  );

  factory CommentItem.fromJson(Map<String, dynamic> srcJson) =>
      _$CommentItemFromJson(srcJson);

  Map<String, dynamic> toJson() => _$CommentItemToJson(this);
}
