import 'dart:ui';

import 'package:flutter/material.dart';
import 'demo/DemosEntrance.dart';
import 'tinysports/TinySportsEntrance.dart';
import 'utils/Loger.dart';

void main() {
//  DartGrammarTest().doFutureTest();
  llogd('Flutter:main()', 'route name=${window.defaultRouteName}');
  if (window.defaultRouteName == '999' ||
      window.defaultRouteName == '/' ||
//      window.defaultRouteName.contains('[sS]port')
//      window.defaultRouteName.contains(new RegExp(r'[sS]port'))
      window.defaultRouteName
          .contains(new RegExp('sport', caseSensitive: false))) {
    runApp(new TinySportsEntrance());
  } else {
    runApp(new DemosEntrance());
  }
}
