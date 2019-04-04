import 'package:json_annotation/json_annotation.dart';

part 'match_player_info.g.dart';

@JsonSerializable()
class MatchPlayerInfo extends Object {
  String playerId;

  String name;

  String icon;

  String position;

  String jerseyNum;

  String playerUrl;

  MatchPlayerInfo(
    this.playerId,
    this.name,
    this.icon,
    this.position,
    this.jerseyNum,
    this.playerUrl,
  );

  factory MatchPlayerInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$MatchPlayerInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$MatchPlayerInfoToJson(this);
}
