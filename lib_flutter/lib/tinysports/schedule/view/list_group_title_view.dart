import 'package:flutter/material.dart';

class ListGroupTitleView extends StatelessWidget {
  final String groupTitle;

  ListGroupTitleView(this.groupTitle);

  @override
  Widget build(BuildContext context) {
    return new Container(
      padding: const EdgeInsets.fromLTRB(10, 2, 0, 2),
      color: Colors.black12,
      child: Text('$groupTitle'),
    );
  }
}
