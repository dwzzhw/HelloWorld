import '../../../http/base_data_model.dart';
import '../../../http/multi_data_model.dart';
import '../../../http/net_request_listener.dart';
import '../../../tinysports/comment/data/comment_list_page_info.dart';
import '../../../tinysports/comment/model/comment_list_info_model.dart';
import '../../../tinysports/match/data/match_detail_related_info.dart';
import '../../../tinysports/match/model/match_detail_related_info_model.dart';

class MatchDetailPrePostModel extends MultiDataModel {
  String mid;
  String targetId;
  MatchDetailRelatedInfoModel matchDetailRelatedInfoModel;
  CommentListInfoModel commentListInfoModel;

  MatchDetailRelatedInfo matchDetailRelatedInfo;
  CommentListPageInfo commentListPageInfo;

  MatchDetailPrePostModel(String mid, String targetId,
      OnMultiDataModelComplete lastCompleteListener)
      : super(null, null, lastCompleteListener) {
    this.mid = mid;
    this.targetId = targetId;
    modelCompleteListener = onDataModelComplete;
    modelErrorListener = onDataModelError;

    matchDetailRelatedInfoModel = MatchDetailRelatedInfoModel(mid, null, null);
    commentListInfoModel = CommentListInfoModel(targetId, null, null);
    addMainDataModel(matchDetailRelatedInfoModel);
    addDataModel(commentListInfoModel);
  }

  void onDataModelComplete<T>(BaseDataModel dataModel, int dataType) {
    if (dataModel is MatchDetailRelatedInfoModel) {
      matchDetailRelatedInfo = dataModel.mRespData;
    } else if (dataModel is CommentListInfoModel) {
      commentListPageInfo = dataModel.mRespData;
    }
  }

  void onDataModelError(
      BaseDataModel dataModel, int code, String errorMsg, int dataType) {}

  @override
  String getLogTAG() {
    return 'MatchDetailPrePostModel';
  }
}
