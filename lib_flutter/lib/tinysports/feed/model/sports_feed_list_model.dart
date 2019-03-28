import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/http/post_data_model.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';
import 'package:lib_flutter/utils/Loger.dart';

class SportsFeedListModel extends PostDataModel<List<FeedItemDetailInfo>> {
  String TAG = 'SportsFeedListModel';
  String idListStr;

  SportsFeedListModel(this.idListStr, OnDataCompleteFunc onCompleteFunc,
      OnDataErrorFunc onErrorFunc)
      : super(onCompleteFunc, onErrorFunction: onErrorFunc);

  @override
  String getUrl() {
    return 'http://preapp.sports.qq.com/feed/list';
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map) {
      logd(TAG, '-->parseDataContentObj(), dataObj is Map');
      Map<String, FeedItemContent> parsedItemMap =
          (dataObj as Map<String, dynamic>).map(
        (key, entry) => MapEntry(
            key, entry == null ? null : FeedItemContent.fromJson(entry)),
      );

      mRespData = List();

      parsedItemMap.forEach((key, itemContent) {
        if (itemContent != null && itemContent.info != null) {
          mRespData.add(itemContent.info);
        }
      });
    } else if (dataObj is List) {
      logd(TAG, '-->parseDataContentObj(), dataObj is List');
    }
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    logd(TAG, '-->getReqParamMap(), ids=$idListStr');
    paramMap['ids'] = idListStr;
    return paramMap;
  }

//  void fetchFeedList(String idList, Function callback) {
//    Map<String, String> params = Map();
//    params['ids'] = idList;
//    HttpController.post('http://preapp.sports.qq.com/feed/list', (respBody) {
//      parseFeedListJson(callback, respBody);
//    }, params: params);
////    return compute(parseFeedListJson, response.body);
//  }
//
//  List<FeedItemDetailInfo> parseFeedListJson(
//      Function callback, String jsonStr) {
////  logd(TAG, '-->parseFeedListJson()');
//    final parsedMap = jsonDecode(jsonStr);
//    FeedList feedListPO = FeedList.fromJson(parsedMap);
//    List<FeedItemDetailInfo> result = feedListPO?.getFeedDetailInfoList();
//    logd('FeedListModel', '-->parseFeedListJson() done, result=$result');
//    if (callback != null) {
//      callback(result);
//    }
//    return result;
//  }
}
