import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/user_info.dart';

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

  static CommentItem mockMyCommentItem(String contentStr) {
    Userinfo userInfo = Userinfo(
        '',
        '本机用户',
        'http://q2.qlogo.cn/g?b=qq&k=UFm7k74RiaPn5pEKx34rpgA&s=100&t=1438693048',
        '1',
        null,
        null,
        null,
        null);
    return new CommentItem(
        null,
        '1',
        null,
        null,
        DateTime.now().millisecondsSinceEpoch.toString(),
        contentStr,
        '0',
        '0',
        '0',
        userInfo);
  }
}
