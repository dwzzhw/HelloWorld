import 'dart:ui';

import 'package:flutter/material.dart';
import '../tinysports/TinySportsRouteManager.dart';

class TinySportsEntrance extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    TinySportsRouteManager routeManager = TinySportsRouteManager();
    routeManager.initRouteWidgetList();
    return MaterialApp(
      home: routeManager.getTargetWidget(window.defaultRouteName),
    );
  }
}
