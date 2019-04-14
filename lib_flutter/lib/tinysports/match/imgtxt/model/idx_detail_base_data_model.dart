import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:lib_flutter/utils/common_utils.dart';

abstract class IdxDetailBaseDataModel<T> {
  static const String TAG = 'IdxDetailBaseDataModel';
  static const int ITEM_CNT_PER_PAGE = 30;
  BaseDataModel indexModel;
  BaseDataModel detailModel;
  List<String> indexList;
  Map<String, T> cachedDetailDataMap;
  int nextReqPageIndex = 0; //下次请求应该从第几页开始

  OnPageDataReadyFunc<T> dataListener;

  IdxDetailBaseDataModel(OnPageDataReadyFunc<T> dataListener) {
    indexList = List();
    cachedDetailDataMap = Map();
    this.dataListener = dataListener;
  }

  void loadData() {
    _initIndexModel();
    nextReqPageIndex = 0;
    if (indexModel != null) {
      indexModel.loadData();
    }
  }

  void _initIndexModel() {
    if (indexModel == null) {
      indexModel = onCreateIndexModel();
      indexModel.onCompleteFunction = (dataModel, dataType) {
        indexList = getIndexList();
        loadNextPageDetailData();
      };
    }
  }

  void _initDetailModel() {
    if (detailModel == null) {
      detailModel = onCreateDetailModel();
      detailModel.onCompleteFunction = (dataModel, dataType) {
        Map<String, T> newPageDetailList = getLatestPageDetailRespMap();
        if (newPageDetailList != null) {
          cachedDetailDataMap.addAll(newPageDetailList);

          notifyNextPageReady();
        }
      };
    }
  }

  void loadNextPageDetailData() {
    Loger.d(TAG, '-->loadNextPageDetailData(), pageIndex=$nextReqPageIndex');
    _initDetailModel();
    if (detailModel != null) {
      String idList = getNotReadyNextPageIndexList();
      if (!CommonUtils.isEmptyStr(idList)) {
        updateNextDetailReqIds(idList);
        Loger.d(TAG,
            '-->loadNextPageDetailData(), pageIndex=$nextReqPageIndex, idList=$idList');
        detailModel.loadData();
      } else {
        notifyNextPageReady();
      }
    }
  }

  void notifyNextPageReady() {
    bool isPageOver =
        (nextReqPageIndex + 1) * ITEM_CNT_PER_PAGE >= indexList.length;
    List<T> pageItems = getDetailDataListForPage(nextReqPageIndex);
    if (dataListener != null) {
      dataListener(pageItems, nextReqPageIndex, isPageOver);
    }
    Loger.d(TAG,
        '-->notifyNextPageReady(), pageIndex=$nextReqPageIndex, item cnt=${pageItems.length}, isPageOver=$isPageOver');
    nextReqPageIndex++;
  }

  List<T> getDetailDataListForPage(int pageIndex) {
    List<T> detailList = List();
    for (int i = pageIndex * ITEM_CNT_PER_PAGE;
        i < (pageIndex + 1) * ITEM_CNT_PER_PAGE && i < indexList.length;
        i++) {
      String id = indexList[i];
      T detailDataItem = cachedDetailDataMap[id];
      if (detailDataItem != null) {
        detailList.add(detailDataItem);
      }
    }
    return detailList;
  }

  String getNotReadyNextPageIndexList() {
    String idList = '';
    for (int i = nextReqPageIndex * ITEM_CNT_PER_PAGE;
        i < (nextReqPageIndex + 1) * ITEM_CNT_PER_PAGE && i < indexList.length;
        i++) {
      String id = indexList[i];
      if (!cachedDetailDataMap.containsKey(id)) {
        if (!CommonUtils.isEmptyStr(idList)) {
          idList += ',';
        }
        idList += id;
      }
    }
    Loger.d(TAG,
        '-->getNextPageIndexList(), nextReqPageIndex=$nextReqPageIndex, idList=$idList');
    return idList;
  }

  List<String> getIndexList();

  void updateNextDetailReqIds(String idList);

  //获取最新一页的详情数据
  Map<String, T> getLatestPageDetailRespMap();

  BaseDataModel onCreateIndexModel();

  BaseDataModel onCreateDetailModel();
}

typedef void OnPageDataReadyFunc<T>(
    List<T> newPageData, int pageIndex, bool isPageOver);
