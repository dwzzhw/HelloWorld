import 'package:flutter/material.dart';
import 'package:lib_flutter/utils/Loger.dart';

abstract class SportsBaseStatelessPage extends StatelessWidget {
  String getLogTAG() {
    return '';
  }

  void log(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }
}
