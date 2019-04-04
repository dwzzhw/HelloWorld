import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/match/data/match_detail_related_info.dart';

class MatchDetailRelatedInfoModel
    extends BaseDataModel<MatchDetailRelatedInfo> {
  String mid;
  bool needMatchDetailInfo;

  MatchDetailRelatedInfoModel(
      this.mid,
      OnDataCompleteFunc<MatchDetailRelatedInfo> onCompleteFunction,
      OnDataErrorFunc onErrorFunction,
      {this.needMatchDetailInfo = false})
      : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'match_detail_related_info_$mid';
  }

  @override
  String getUrl() {
    return 'https://app.sports.qq.com/match/detailRelatedInfo';
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    log('-->getReqParamMap(), id=$mid');
    paramMap['mid'] = mid;
    paramMap['needMatchDetail'] = needMatchDetailInfo ? '1' : '0';
    return paramMap;
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      mRespData = MatchDetailRelatedInfo.fromJson(dataObj);
    }
  }

  @override
  String getLogTAG() {
    return 'MatchDetailRelatedInfoModel';
  }
}
