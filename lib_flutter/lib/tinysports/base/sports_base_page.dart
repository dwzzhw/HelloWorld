import 'package:flutter/material.dart';
import '../../utils/Loger.dart';

abstract class SportsBasePage extends StatefulWidget {
  String getLogTAG() {
    return '$runtimeType';
  }

  void llog(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }
}
