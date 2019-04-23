import 'package:flutter/material.dart';
import '../../utils/Loger.dart';

abstract class SportsBaseStatelessPage extends StatelessWidget {
  String getLogTAG() {
    return '';
  }

  void log(String logMsg) {
    llogd(getLogTAG(), logMsg);
  }
}
