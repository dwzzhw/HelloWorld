import 'package:json_annotation/json_annotation.dart';
import '../../../tinysports/base/data/jump_data.dart';
import '../../../utils/Loger.dart';

part 'feedindex.g.dart';

@JsonSerializable()
class FeedIndex extends Object {
  @JsonKey(name: 'code')
  int code;

  @JsonKey(name: 'data')
  FeedIndexData data;

  @JsonKey(name: 'version')
  String version;

  FeedIndex(
    this.code,
    this.data,
    this.version,
  );

  factory FeedIndex.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedIndexFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedIndexToJson(this);

  List<FeedIndexItem> getFeedIndexList() {
    llogd('FeedIndexPO', '-->getFeedIndexList, data=$data');
    return data?.list;
  }
}

@JsonSerializable()
class FeedIndexData extends Object {
  @JsonKey(name: 'marquee')
  List<Marquee> marquee;

  @JsonKey(name: 'list')
  List<FeedIndexItem> list;

  @JsonKey(name: 'hasMore')
  String hasMore;

  @JsonKey(name: 'curVersion')
  String curVersion;

  @JsonKey(name: 'interval')
  String interval;

  @JsonKey(name: 'lastFeedId')
  String lastFeedId;

  @JsonKey(name: 'ruleId')
  String ruleId;

  @JsonKey(name: 'ruleTimeVersion')
  String ruleTimeVersion;

  FeedIndexData(
    this.marquee,
    this.list,
    this.hasMore,
    this.curVersion,
    this.interval,
    this.lastFeedId,
    this.ruleId,
    this.ruleTimeVersion,
  );

  factory FeedIndexData.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedIndexDataFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedIndexDataToJson(this);
}

@JsonSerializable()
class Marquee extends Object {
  @JsonKey(name: 'title')
  String title;

  @JsonKey(name: 'jumpData')
  JumpData jumpData;

  Marquee(
    this.title,
    this.jumpData,
  );

  factory Marquee.fromJson(Map<String, dynamic> srcJson) =>
      _$MarqueeFromJson(srcJson);

  Map<String, dynamic> toJson() => _$MarqueeToJson(this);
}

@JsonSerializable()
class FeedIndexItem extends Object {
  @JsonKey(name: 'id')
  String id;

  @JsonKey(name: 'Pos')
  String pos;

  @JsonKey(name: 'columnId')
  String columnId;

  @JsonKey(name: 'session')
  String session;

  @JsonKey(name: 'report')
  String report;

  FeedIndexItem(
    this.id,
    this.pos,
    this.columnId,
    this.session,
    this.report,
  );

  factory FeedIndexItem.fromJson(Map<String, dynamic> srcJson) =>
      _$FeedIndexItemFromJson(srcJson);

  Map<String, dynamic> toJson() => _$FeedIndexItemToJson(this);
}
