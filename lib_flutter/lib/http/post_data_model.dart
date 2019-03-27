import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/net_request_listener.dart';

class PostDataModel extends BaseDataModel{
  PostDataModel(NetRequestListener mListener) : super(mListener);

  @override
  String getUrl() {
    return null;
  }

  @override
  parseDataContentObj(dataObj) {
    return null;
  }

}