import 'package:flutter/material.dart';
import 'package:lib_flutter/utils/Loger.dart';

abstract class SportsBasePageState<T extends StatefulWidget> extends State<T> {
  bool isSuccess = true;
  String errTipsMsg;

  void onFetchDataError(String errorMsg) {
    logd(getLogTAG(), '-->_onFetchDataError(), errorMsg=$errorMsg');
    setState(() {
      isSuccess = false;
      errTipsMsg = errorMsg;
    });
  }

  String getLogTAG();

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return null;
  }

  void log(String logMsg) {
    logd(getLogTAG(), logMsg);
  }
}
