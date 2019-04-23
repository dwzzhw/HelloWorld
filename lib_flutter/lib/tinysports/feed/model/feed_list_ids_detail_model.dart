import '../../../http/base_data_model.dart';
import '../../../http/idx_detail_base_data_model.dart';
import '../../../tinysports/base/data/news_item.dart';
import '../../../tinysports/feed/model/sports_feed_index_model.dart';
import '../../../tinysports/feed/model/sports_feed_list_model.dart';

class FeedListIdsDetailModel extends IdxDetailBaseDataModel<NewsItem> {
  String columnId;

  FeedListIdsDetailModel(
      this.columnId, OnPageDataReadyFunc<NewsItem> dataListener)
      : super(dataListener);

  @override
  List<String> getIndexList() {
    List<String> idList;
    if (indexModel is SportsFeedIndexModel) {
      idList = (indexModel as SportsFeedIndexModel).getIdList();
    }
    return idList;
  }

  @override
  Map<String, NewsItem> getLatestPageDetailRespMap() {
    Map<String, NewsItem> resultMap;
    if (detailModel is SportsFeedListModel) {
      resultMap = (detailModel as SportsFeedListModel).mRespData;
    }
    return resultMap;
  }

  @override
  BaseDataModel onCreateDetailModel() {
    return SportsFeedListModel(null, null, null);
  }

  @override
  BaseDataModel onCreateIndexModel() {
    return SportsFeedIndexModel(columnId, null, null);
  }

  @override
  void updateNextDetailReqIds(String idList) {
    if (detailModel is SportsFeedListModel) {
      (detailModel as SportsFeedListModel).idListStr = idList;
    }
  }
}
