import 'package:flutter/material.dart';
import 'package:lib_flutter/utils/Loger.dart';

abstract class SportsBasePage extends StatefulWidget {
  String getLogTAG() {
    return '';
  }

  void llog(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }
}
