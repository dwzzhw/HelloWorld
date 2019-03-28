import 'package:flutter/material.dart';
import 'package:lib_flutter/tinysports/base/data/match_info.dart';
import 'package:lib_flutter/tinysports/base/data/schedule_info.dart';
import 'package:lib_flutter/tinysports/feed/data/feedlist.dart';

class ScheduleItemView extends StatelessWidget {
  final ScheduleInfo scheduleInfo;

  ScheduleItemView(this.scheduleInfo);

  @override
  Widget build(BuildContext context) {
    MatchInfo matchInfo = scheduleInfo.matchInfo;
    return new Container(
      padding: const EdgeInsets.all(10.0),
      child: new Row(
        children: [
          new Expanded(
            child: new Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                new Container(
                  padding: const EdgeInsets.only(bottom: 8.0),
                  child: new Text(
                    '${matchInfo.leftName} VS ${matchInfo.rightName}',
                    style: new TextStyle(
                      fontWeight: FontWeight.bold,
                    ),
                    maxLines: 2,
                  ),
                ),
                new Text(
                  '${matchInfo.matchDesc}',
                  style: new TextStyle(
                    color: Colors.grey[500],
                  ),
                  maxLines: 1,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
