import '../../../http/base_data_model.dart';
import '../../../http/net_request_listener.dart';
import '../../../tinysports/base/data/column_info.dart';

class ColumnInfoListModel extends BaseDataModel<ColumnInfoList> {
  ColumnInfoListModel(OnDataCompleteFunc<ColumnInfoList> onCompleteFunction,
      OnDataErrorFunc onErrorFunction)
      : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'column_info_list';
  }

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/match/columnsV45';
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map) {
      mRespData = ColumnInfoList.fromJson(dataObj);
    }
  }

  @override
  String getLogTAG() {
    return 'ColumnInfoListModel';
  }
}
