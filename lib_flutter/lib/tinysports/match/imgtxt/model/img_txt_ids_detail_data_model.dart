import '../../../../http/base_data_model.dart';
import '../../../../tinysports/match/imgtxt/data/img_txt_item_detail_info.dart';
import '../../../../http/idx_detail_base_data_model.dart';
import '../../../../tinysports/match/imgtxt/model/img_txt_detail_model.dart';
import '../../../../tinysports/match/imgtxt/model/img_txt_ids_model.dart';

class ImgTxtIdsDetailDataModel
    extends IdxDetailBaseDataModel<ImgTxtItemDetail> {
  String mid;

  ImgTxtIdsDetailDataModel(this.mid, OnPageDataReadyFunc<ImgTxtItemDetail> listener)
      : super(listener);

  @override
  List<String> getIndexList() {
    List<String> idList;
    if (indexModel is ImgTxtIdsModel) {
      idList = (indexModel as ImgTxtIdsModel).getIdList();
    }
    return idList;
  }

  @override
  Map<String, ImgTxtItemDetail> getLatestPageDetailRespMap() {
    Map<String, ImgTxtItemDetail> resultMap;
    if (detailModel is ImgTxtDetailModel) {
      resultMap = (detailModel as ImgTxtDetailModel).mRespData;
    }
    return resultMap;
  }

  @override
  BaseDataModel onCreateDetailModel() {
    return ImgTxtDetailModel(mid, null, null, null);
  }

  @override
  BaseDataModel onCreateIndexModel() {
    return ImgTxtIdsModel(mid, null, null);
  }

  @override
  void updateNextDetailReqIds(String idList) {
    if (detailModel is ImgTxtDetailModel) {
      (detailModel as ImgTxtDetailModel).updateIdList(idList);
    }
  }
}
