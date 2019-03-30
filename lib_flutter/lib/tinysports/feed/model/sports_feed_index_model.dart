import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';

class SportsFeedIndexModel extends BaseDataModel<FeedIndexData> {
  SportsFeedIndexModel(OnDataCompleteFunc<FeedIndexData> mCompleteFunction,
      OnDataErrorFunc mErrorFunction)
      : super(mCompleteFunction, onErrorFunction: mErrorFunction);

  void setCompleteCallbackFunc(Function completeFunc) {
    onCompleteFunction = completeFunc;
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

  @override
  String getCacheKey() {
    return 'feed_index_list_hot';
  }
}
