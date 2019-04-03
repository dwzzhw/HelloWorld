import 'package:json_annotation/json_annotation.dart';

part 'user_info.g.dart';

@JsonSerializable()
class Userinfo extends Object {
  String userid;

  String nick;

  String head;

  String gender;

  String region;

  String uidex;

  String upnum;

  String commentnum;

  Userinfo(
    this.userid,
    this.nick,
    this.head,
    this.gender,
    this.region,
    this.uidex,
    this.upnum,
    this.commentnum,
  );

  factory Userinfo.fromJson(Map<String, dynamic> srcJson) =>
      _$UserinfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$UserinfoToJson(this);
}
