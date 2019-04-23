import '../http/base_data_model.dart';
import '../http/net_request_listener.dart';
import '../utils/Loger.dart';
import '../utils/common_utils.dart';

class MultiDataModel {
  static const int REQ_STATUS_LOADING = 0;
  static const int REQ_STATUS_SUCCESS = 1;
  static const int REQ_STATUS_FAIL = 2;

  OnDataModelComplete modelCompleteListener;
  OnDataModelError modelErrorListener;

  OnMultiDataModelComplete multiDataModelLastCompleteListener;

  List<BaseDataModel> mainDataModelList;
  List<BaseDataModel> normalDataModelList;

  Map<BaseDataModel, int> dataModelReqStatus;

  String lastErrorMsg;
  int lastErrorCode;

  MultiDataModel(
      OnDataModelComplete modelCompleteListener,
      OnDataModelError modelErrorListener,
      OnMultiDataModelComplete lastCompleteListener) {
    this.modelCompleteListener = modelCompleteListener;
    this.modelErrorListener = modelErrorListener;
    this.multiDataModelLastCompleteListener = lastCompleteListener;
    dataModelReqStatus = Map();
  }

  void addDataModel(BaseDataModel dataModel) {
    if (normalDataModelList == null) {
      normalDataModelList = List();
    }
    if (!normalDataModelList.contains(dataModel)) {
      dataModel.onCompleteFunction = this.onDataCompleteFunc;
      dataModel.onErrorFunction = this.onDataErrorFunc;
      normalDataModelList.add(dataModel);
    }
  }

  void addMainDataModel(BaseDataModel dataModel) {
    addDataModel(dataModel);
    if (mainDataModelList == null) {
      mainDataModelList = List();
    }
    if (!mainDataModelList.contains(dataModel)) {
      mainDataModelList.add(dataModel);
    }
  }

  void loadData() {
    if (normalDataModelList != null) {
      normalDataModelList.forEach((BaseDataModel dataModel) {
        dataModelReqStatus[dataModel] = REQ_STATUS_LOADING;
      });
      normalDataModelList.forEach((BaseDataModel dataModel) {
        dataModel.loadData();
      });
    }
  }

  void onDataCompleteFunc(BaseDataModel dataModel, int dataType) {
    notifyDataModelComplete(dataModel, dataType);
    updateDataModelStatus(dataModel, REQ_STATUS_SUCCESS, dataType);
  }

  void onDataErrorFunc(
      BaseDataModel dataModel, int code, String errorMsg, int dataType) {
    lastErrorCode = code;
    lastErrorMsg = errorMsg;
    notifyDataModelError(dataModel, code, errorMsg, dataType);
    updateDataModelStatus(dataModel, REQ_STATUS_FAIL, dataType);
  }

  void updateDataModelStatus(
      BaseDataModel dataModel, int newStatus, int dataType) {
    if (dataModel != null) {
      int oldStatus = dataModelReqStatus[dataModel];
      log('-->updateDataModelStatus(), model=$dataModel, newStatus=$newStatus, oldStatus=$oldStatus');
      if (newStatus == REQ_STATUS_SUCCESS || oldStatus == REQ_STATUS_LOADING) {
        dataModelReqStatus[dataModel] = newStatus;
      }
    }
    checkToNotifyLastResult(dataType);
  }

  void checkToNotifyLastResult(int lastDataType) {
    int lastResult = REQ_STATUS_SUCCESS;
    if (dataModelReqStatus != null && normalDataModelList != null) {
      for (BaseDataModel dataModel in normalDataModelList) {
        int status = dataModelReqStatus[dataModel];
        if (status == REQ_STATUS_LOADING) {
          lastResult = REQ_STATUS_LOADING;
          break;
        } else if (status == REQ_STATUS_FAIL &&
            (isMainDataModel(dataModel) ||
                CommonUtils.isListEmpty(mainDataModelList))) {
          lastResult = REQ_STATUS_FAIL;
        }
      }
    }

    if (lastResult == REQ_STATUS_SUCCESS) {
      if (multiDataModelLastCompleteListener != null) {
        multiDataModelLastCompleteListener(this, lastDataType, true);
      }
    } else if (lastResult == REQ_STATUS_FAIL) {
      if (multiDataModelLastCompleteListener != null) {
        multiDataModelLastCompleteListener(this, lastDataType, false);
      }
    }
  }

  bool isMainDataModel(BaseDataModel dataModel) {
    return mainDataModelList != null && mainDataModelList.contains(dataModel);
  }

  void notifyDataModelComplete(BaseDataModel dataModel, int dataType) {
    log('-->notifyDataModelComplete(), dataModel=$dataModel, dataType=$dataType, modelCompleteListener=$modelCompleteListener');
    if (modelCompleteListener != null) {
      modelCompleteListener(dataModel, dataType);
    }
  }

  void notifyDataModelError(
      BaseDataModel dataModel, int code, String errorMsg, int dataType) {
    log('-->notifyDataModelComplete(), dataModel=$dataModel, code=$code, errorMsg=$errorMsg, dataType=$dataType, modelCompleteListener=$modelCompleteListener');
    if (notifyDataModelError != null) {
      modelErrorListener(dataModel, code, errorMsg, dataType);
    }
  }

  void log(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }

  String getLogTAG() {
    return 'MultiDataModel';
  }
}
