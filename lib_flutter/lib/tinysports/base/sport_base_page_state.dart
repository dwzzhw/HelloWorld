import 'package:flutter/material.dart';
import '../../utils/Loger.dart';

abstract class SportsBasePageState<T extends StatefulWidget> extends State<T> {
  bool isSuccess = true;
  String errTipsMsg;

  void onFetchDataError(String errorMsg) {
    llogd(getLogTAG(), '-->_onFetchDataError(), errorMsg=$errorMsg');
    setState(() {
      isSuccess = false;
      errTipsMsg = errorMsg;
    });
  }

  String getLogTAG(){
    return '$runtimeType';
  }

  @override
  Widget build(BuildContext context) {
    return null;
  }

  void llog(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }
}
