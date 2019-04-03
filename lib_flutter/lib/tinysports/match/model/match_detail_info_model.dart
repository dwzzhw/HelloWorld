import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';

class MatchDetailInfoModel extends BaseDataModel{
  MatchDetailInfoModel(OnDataCompleteFunc onCompleteFunction, OnDataErrorFunc onErrorFunction) : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    // TODO: implement getCacheKey
    return null;
  }

  @override
  String getUrl() {
    // TODO: implement getUrl
    return null;
  }

  @override
  void parseDataContentObj(dataObj) {
    // TODO: implement parseDataContentObj
  }

}