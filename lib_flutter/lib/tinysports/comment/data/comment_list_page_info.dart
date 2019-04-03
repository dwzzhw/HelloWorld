import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/comment/data/comment_list_content_info.dart';

part 'comment_list_page_info.g.dart';

@JsonSerializable()
class CommentListPageInfo extends Object {
  String total;

  String maxid;

  CommentListContentInfo comment;

  String title;

  CommentListPageInfo(
    this.total,
    this.maxid,
    this.comment,
    this.title,
  );

  factory CommentListPageInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$CommentListPageInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$CommentListPageInfoToJson(this);
}
