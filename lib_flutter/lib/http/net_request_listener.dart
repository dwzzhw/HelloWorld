import 'package:lib_flutter/http/base_data_model.dart';
import 'package:lib_flutter/http/multi_data_model.dart';

typedef void OnDataCompleteFunc<T>(BaseDataModel<T> dataModel, int dataType);
typedef void OnDataErrorFunc(
    BaseDataModel dataModel, int code, String errorMsg, int dataType);

///Multi data model 的分步执行结果
typedef void OnDataModelComplete<T>(BaseDataModel<T> dataModel, int dataType);
typedef void OnDataModelError<T>(
    BaseDataModel<T> dataModel, int code, String errorMsg, int dataType);

///Multi data model 的最终执行结果
typedef void OnMultiDataModelComplete(
    MultiDataModel multiDataModel, int lastDataType, bool success);
