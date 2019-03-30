import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';
import 'package:lib_flutter/tinysports/profile/data/profile_page_info.dart';

class ProfilePageModel extends BaseDataModel<ProfilePageInfo> {
  ProfilePageModel(OnDataCompleteFunc<ProfilePageInfo> onCompleteFunction,
      OnDataErrorFunc onErrorFunc)
      : super(onCompleteFunction, onErrorFunction: onErrorFunc);

  @override
  String getUrl() {
    return 'http://app.sports.qq.com/user/homePage?appvid=6.0';
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      mRespData = ProfilePageInfo.fromJson(dataObj);
    }
  }

  @override
  String getLogTAG() {
    return 'ProfilePageModel';
  }

  @override
  String getCacheKey() {
    return 'profile_page_data';
  }

  @override
  bool needCache() {
    return true;
  }
}
