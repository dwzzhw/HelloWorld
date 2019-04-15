import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/http/post_data_model.dart';
import 'package:lib_flutter/tinysports/base/data/news_item.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';

class SportsFeedListModel extends PostDataModel<Map<String, NewsItem>> {
  String idListStr;

  SportsFeedListModel(this.idListStr, OnDataCompleteFunc onCompleteFunc,
      OnDataErrorFunc onErrorFunc)
      : super(onCompleteFunc, onErrorFunction: onErrorFunc);

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/feed/list';
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map) {
      log('-->parseDataContentObj(), dataObj is Map');
      Map<String, FeedItemContent> parsedItemMap =
          (dataObj as Map<String, dynamic>).map(
        (key, entry) => MapEntry(
            key, entry == null ? null : FeedItemContent.fromJson(entry)),
      );

      mRespData = Map();

      parsedItemMap.forEach((key, itemContent) {
        if (itemContent != null && itemContent.info != null) {
          mRespData[key]=itemContent.info;
        }
      });
    } else if (dataObj is List) {
      log('-->parseDataContentObj(), dataObj is List');
    }
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    log('-->getReqParamMap(), ids=$idListStr');
    paramMap['ids'] = idListStr;
    return paramMap;
  }

  @override
  String getCacheKey() {
    return 'feed_detail_list_hot_${idListStr.hashCode})';
  }

  @override
  String getLogTAG() {
    return 'SportsFeedListModel';
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
