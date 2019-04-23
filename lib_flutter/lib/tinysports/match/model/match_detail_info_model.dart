import '../../../http/base_data_model.dart';
import '../../../http/net_request_listener.dart';
import '../../../tinysports/match/data/match_detail_info.dart';

class MatchDetailInfoModel extends BaseDataModel<MatchDetailInfo> {
  String mid;

  MatchDetailInfoModel(
      this.mid,
      OnDataCompleteFunc<MatchDetailInfo> onCompleteFunction,
      OnDataErrorFunc onErrorFunction)
      : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'match_detail_info_$mid';
  }

  @override
  String getUrl() {
    return 'https://app.sports.qq.com/match/detail';
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    log('-->getReqParamMap(), id=$mid');
    paramMap['mid'] = mid;
    return paramMap;
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      mRespData = MatchDetailInfo.fromJson(dataObj);
    }
  }

  @override
  String getLogTAG() {
    return 'MatchDetailInfoModel';
  }
}