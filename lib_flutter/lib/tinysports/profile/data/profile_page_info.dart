import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/clickable_img.dart';
import 'package:lib_flutter/tinysports/base/data/jump_data.dart';

part 'profile_page_info.g.dart';

@JsonSerializable()
class ProfilePageInfo extends Object {
  List<ProfileItemEntrance> vEntrance;

  List<KEntranceGroup> kEntrance;

  List<ClickableImage> marquee;

  List<ProfileItemEntrance> walletEntrance;

  String dailyCheckPop;

  String newRewardPop;

  UserInfo userInfo;

  ProfileConfig config;

  ProfilePageInfo(
    this.vEntrance,
    this.kEntrance,
    this.marquee,
    this.walletEntrance,
    this.dailyCheckPop,
    this.newRewardPop,
    this.userInfo,
    this.config,
  );

  factory ProfilePageInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$ProfilePageInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ProfilePageInfoToJson(this);
}

@JsonSerializable()
class KEntranceGroup extends Object {
  String title;

  String type;

  List<ProfileItemEntrance> list;

  KEntranceGroup(
    this.title,
    this.type,
    this.list,
  );

  factory KEntranceGroup.fromJson(Map<String, dynamic> srcJson) =>
      _$KEntranceGroupFromJson(srcJson);

  Map<String, dynamic> toJson() => _$KEntranceGroupToJson(this);
}

@JsonSerializable()
class ProfileItemEntrance extends Object {
  String type;

  String name;

  String subName;

  String subNameColor;

  String redDot;

  String autoErase;

  String logo;

  JumpData jumpData;

  ProfileItemEntrance(
    this.type,
    this.name,
    this.subName,
    this.subNameColor,
    this.redDot,
    this.autoErase,
    this.logo,
    this.jumpData,
  );

  factory ProfileItemEntrance.fromJson(Map<String, dynamic> srcJson) =>
      _$ProfileItemEntranceFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ProfileItemEntranceToJson(this);
}

@JsonSerializable()
class UserInfo extends Object {
  String isVip;

  List<dynamic> service;

  List<dynamic> clubs;

  String inform;

  String serviceEndTime;

  UserInfo(
    this.isVip,
    this.service,
    this.clubs,
    this.inform,
    this.serviceEndTime,
  );

  factory UserInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$UserInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$UserInfoToJson(this);
}

@JsonSerializable()
class ProfileConfig extends Object {
  String wallet;

  String nologinBuy;

  String noLoginVip;

  ProfileConfig(
    this.wallet,
    this.nologinBuy,
    this.noLoginVip,
  );

  factory ProfileConfig.fromJson(Map<String, dynamic> srcJson) =>
      _$ProfileConfigFromJson(srcJson);

  Map<String, dynamic> toJson() => _$ProfileConfigToJson(this);
}
