import 'package:flutter/material.dart';

void printLog(String logMsg) {
  debugPrint('[Flutter_d_dwz] -- ' + logMsg);
}

void logd(String tag, String logMsg) {
  debugPrint('[Flutter_d_dwz--$tag] -- ' + logMsg);
}

void log(String logMsg) {
  print('[Flutter_dwz] -- ' + logMsg);
}
