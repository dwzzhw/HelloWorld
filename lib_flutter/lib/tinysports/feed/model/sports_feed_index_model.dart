import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';

class SportsFeedIndexModel extends BaseDataModel<FeedIndexData> {
  String columnId;

  SportsFeedIndexModel(
      this.columnId,
      OnDataCompleteFunc<FeedIndexData> mCompleteFunction,
      OnDataErrorFunc mErrorFunction)
      : super(mCompleteFunction, onErrorFunction: mErrorFunction);

  void setCompleteCallbackFunc(Function completeFunc) {
    onCompleteFunction = completeFunc;
  }

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/feed/index';
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (columnId != null && columnId.length > 0) {
      if (paramMap == null) {
        paramMap = Map<String, String>();
      }
      paramMap['columnId'] = columnId;
    }

    return paramMap;
  }

  @override
  parseDataContentObj(dataObj) {
    mRespData = FeedIndexData.fromJson(dataObj);
  }

  List<FeedIndexItem> getFeedIndexList() {
    return mRespData?.list;
  }

  @override
  String getCacheKey() {
    return 'feed_index_list_$columnId';
  }

  @override
  String getLogTAG() {
    return 'SportsFeedIndexModel';
  }
}
