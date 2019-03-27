import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/feed/data/feedindex.dart';

class SportsFeedIndexModel2 extends BaseDataModel<FeedIndexData> {
  SportsFeedIndexModel2(Function mCompleteFunction) : super(mCompleteFunction);

  void setCompleteCallbackFunc(Function completeFunc){
    mCompleteFunction = completeFunc;
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
