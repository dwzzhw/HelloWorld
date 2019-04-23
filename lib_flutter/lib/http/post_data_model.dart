import '../http/base_data_model.dart';
import '../http/http_controller.dart';
import '../http/net_request_listener.dart';
import '../utils/Loger.dart';

abstract class PostDataModel<T> extends BaseDataModel<T> {
  static String TAG = "PostDataModel";

  PostDataModel(OnDataCompleteFunc onCompleteFunction,
      {OnDataErrorFunc onErrorFunction})
      : super(onCompleteFunction, onErrorFunction);

  void loadDataFromNet() {
    llogd(TAG, '-->loadDataFromNet()');
    HttpController.post(getUrl(), onGetHttpRespBody, params: getReqParamMap(),
        errorCallback: (exception) {
      notifyDataError(BaseDataModel.ERROR_CODE_EXCEPTION, exception.toString(),
          BaseDataModel.LOAD_DATA_TYPE_REFRESH);
    });
  }
}
