import '../../../http/base_data_model.dart';
import '../../../http/net_request_listener.dart';
import '../../../tinysports/comment/data/comment_list_page_info.dart';

class CommentListInfoModel extends BaseDataModel<CommentListPageInfo> {
  String targetId;
  int reqNum;
  int pageFlag;

  CommentListInfoModel(
    this.targetId,
    OnDataCompleteFunc<CommentListPageInfo> onCompleteFunction,
    OnDataErrorFunc onErrorFunction, {
    this.reqNum = 20,
    this.pageFlag = 0,
  }) : super(onCompleteFunction, onErrorFunction);

  @override
  String getCacheKey() {
    return 'comment_list__$targetId';
  }

  @override
  String getUrl() {
    return 'https://app.sports.qq.com/comment';
  }

  @override
  Map<String, String> getReqParamMap() {
    Map<String, String> paramMap = super.getReqParamMap();
    if (paramMap == null) {
      paramMap = Map<String, String>();
    }
    log('-->getReqParamMap(), targetId=$targetId');
    paramMap['reqnum'] = '$reqNum';
    paramMap['pageflag'] = '$pageFlag';
    paramMap['targetId'] = targetId;
    return paramMap;
  }

  @override
  void parseDataContentObj(dataObj) {
    if (dataObj is Map<String, dynamic>) {
      mRespData = CommentListPageInfo.fromJson(dataObj);
    }
  }

  @override
  String getLogTAG() {
    return 'CommentListInfoModel';
  }
}
