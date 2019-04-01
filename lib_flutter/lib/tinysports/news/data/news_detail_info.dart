import 'package:json_annotation/json_annotation.dart';
import 'package:lib_flutter/tinysports/base/data/jump_data.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_item_content.dart';

part 'news_detail_info.g.dart';

@JsonSerializable()
class NewsDetailInfo extends Object {
  static const int ITEM_NORMAL = 0;
  static const int ITEM_MULTI_IMG = 1;// 组图类型
  static const int ITEM_VIDEO = 2;//视频文章类型（atype=2或3）新增vid字段
  static const int ITEM_VIDEO_ONLY = 3;
  static const int ITEM_WEBVIEW = 6; // 链接型文章
  static const int ITEM_SPECIAL = 11;//专题
  static const int ITEM_VIDEO_SPECIAL = 23;//视频专辑

  String newsId;

  String title;

  String abstract;

  String url;

  String imgurl;

  String imgurl1;

  String imgurl2;

  String pub_time;

  String publishTime;

  String atype;

  String newsAppId;

  String source;

  String shareUrl;

  String duration;

  String hasCopyRight;

  String vid;

  String commentsNum;

  String commentId;

  String tag_key;

  String newsMid;

  Map<String, SubjectTag> subjectTags;

  List<Subjects> subjects;

  List<NewsDetailItemContentBase> content;

  String targetId;

  String isDisabled;

  List<String> cmsTags;

  String cmsColumn;

  String cmsSite;

  String topicId;

  String isShare;

  String updateFrequency;

  String relate_from;

  NewsDetailInfo(
    this.newsId,
    this.title,
    this.abstract,
    this.url,
    this.imgurl,
    this.imgurl1,
    this.imgurl2,
    this.pub_time,
    this.publishTime,
    this.atype,
    this.newsAppId,
    this.source,
    this.shareUrl,
    this.duration,
    this.hasCopyRight,
    this.vid,
    this.commentsNum,
    this.commentId,
    this.tag_key,
    this.newsMid,
    this.subjectTags,
    this.subjects,
    this.content,
    this.targetId,
    this.isDisabled,
    this.cmsTags,
    this.cmsColumn,
    this.cmsSite,
    this.topicId,
    this.isShare,
    this.updateFrequency,
    this.relate_from,
  );

  factory NewsDetailInfo.fromJson(Map<String, dynamic> srcJson) =>
      _$NewsDetailInfoFromJson(srcJson);

  Map<String, dynamic> toJson() => _$NewsDetailInfoToJson(this);
}

@JsonSerializable()
class SubjectTag extends Object {
  String id;

  String teamId;

  String name;

  String icon;

  String competitionId;

  String columnId;

  String playerId;

  String moduleId;

  String hasMatch;

  JumpData jumpData;

  SubjectTag(
    this.id,
    this.teamId,
    this.name,
    this.icon,
    this.competitionId,
    this.columnId,
    this.playerId,
    this.moduleId,
    this.hasMatch,
    this.jumpData,
  );

  factory SubjectTag.fromJson(Map<String, dynamic> srcJson) =>
      _$SubjectTagFromJson(srcJson);

  Map<String, dynamic> toJson() => _$SubjectTagToJson(this);
}

@JsonSerializable()
class Subjects extends Object {
  String subjectId;

  String word;

  String weight;

  Subjects(
    this.subjectId,
    this.word,
    this.weight,
  );

  factory Subjects.fromJson(Map<String, dynamic> srcJson) =>
      _$SubjectsFromJson(srcJson);

  Map<String, dynamic> toJson() => _$SubjectsToJson(this);
}
