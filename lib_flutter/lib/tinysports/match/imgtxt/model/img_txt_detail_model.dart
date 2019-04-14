import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/data/img_txt_ids_info.dart';
import 'package:lib_flutter/tinysports/match/imgtxt/data/img_txt_item_detail_info.dart';

class ImgTxtDetailModel extends BaseDataModel<Map<String, ImgTxtItemDetail>> {
  String mid;
  String idListStr;

  ImgTxtDetailModel(
      this.mid,
      this.idListStr,
      OnDataCompleteFunc<Map<String, ImgTxtItemDetail>> onCompleteFunction,
      OnDataErrorFunc onErrorFunction)
      : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'img_txt_detail_$mid';
  }

  void updateIdList(String idList) {
    this.idListStr = idList;
  }

  @override
  bool needCache() {
    return false;
  }

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/textLive/detail';
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      ImgTxtItemDetailInfo detailInfo = ImgTxtItemDetailInfo.fromJson(dataObj);
      mRespData = detailInfo?.detail;
    }
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    log('-->getReqParamMap(), id=$mid');
    paramMap['mid'] = mid;
    paramMap['ids'] = idListStr;
    return paramMap;
  }

  @override
  String getLogTAG() {
    return 'ImgTxtIdsModel';
  }
}
