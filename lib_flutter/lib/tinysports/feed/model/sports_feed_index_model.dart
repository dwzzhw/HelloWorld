import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';

class SportsFeedIndexModel extends BaseDataModel<FeedIndexData> {
  SportsFeedIndexModel(OnDataCompleteFunc<FeedIndexData> mCompleteFunction,
      OnDataErrorFunc mErrorFunction)
      : super(mCompleteFunction, OnErrorFunction: mErrorFunction);

  void setCompleteCallbackFunc(Function completeFunc) {
    OnCompleteFunction = completeFunc;
  }

  @override
  String getUrl() {
    return 'http://preapp.sports.qq.com/feed/index';
  }

  @override
  parseDataContentObj(dataObj) {
    mRespData = FeedIndexData.fromJson(dataObj);
  }

  List<FeedIndexItem> getFeedIndexList() {
    return mRespData?.list;
  }
}
