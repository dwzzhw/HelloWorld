import 'package:flutter/material.dart';

void printLog(String logMsg) {
  debugPrint('[Flutter_d_dwz] -- ' + logMsg);
}

void llogd(String tag, String logMsg) {
  debugPrint('[Flutter_d_dwz--$tag] -- ' + logMsg);
}

void llog(String logMsg) {
  print('[Flutter_dwz] -- ' + logMsg);
}

class Loger {
  static void d(String tag, String logMsg) {
    llogd(tag, logMsg);
  }
}
