import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/news/data/news_detail_info.dart';

class NewsDetailInfoModel extends BaseDataModel<NewsDetailInfo> {
  String newsId;

  NewsDetailInfoModel(
      this.newsId,
      OnDataCompleteFunc<NewsDetailInfo> onCompleteFunction,
      OnDataErrorFunc onErrorFunction)
      : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'news_detail_$newsId';
  }

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/news/detail';
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    log('-->getReqParamMap(), id=$newsId');
    paramMap['id'] = newsId;
    return paramMap;
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      mRespData = NewsDetailInfo.fromJson(dataObj);
    }
  }
}
