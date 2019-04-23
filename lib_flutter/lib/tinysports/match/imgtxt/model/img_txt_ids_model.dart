import '../../../../http/base_data_model.dart';
import '../../../../http/net_request_listener.dart';
import '../../../../tinysports/match/imgtxt/data/img_txt_ids_info.dart';

class ImgTxtIdsModel extends BaseDataModel<ImgTxtIdsInfo> {
  String mid;

  ImgTxtIdsModel(this.mid, OnDataCompleteFunc<ImgTxtIdsInfo> onCompleteFunction,
      OnDataErrorFunc onErrorFunction)
      : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'img_txt_ids_$mid';
  }

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/textLive/index';
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      mRespData = ImgTxtIdsInfo.fromJson(dataObj);
    }
  }

  List<String> getIdList(){
    return mRespData?.index;
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
  String getLogTAG() {
    return 'ImgTxtIdsModel';
  }
}
