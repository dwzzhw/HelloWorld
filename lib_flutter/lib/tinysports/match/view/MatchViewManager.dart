import 'package:flutter/material.dart';
import '../../../tinysports/base/view/common_view_manager.dart';

class MatchViewManager {
  static Widget getMatchView(ViewTypeDataContainer viewInfoContainer) {
    Widget resultView = CommonViewManager.getCommonView(
        viewInfoContainer?.viewType, viewInfoContainer?.data);
    if (resultView == null) {
      resultView =
          Text('Unsupported data type: ${viewInfoContainer?.viewType}');
    }
    return resultView;
  }
}
