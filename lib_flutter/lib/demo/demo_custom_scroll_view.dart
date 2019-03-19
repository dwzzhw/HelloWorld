import 'package:flutter/material.dart';

class DemoCustomScrollView extends StatelessWidget {
  static final routeName = 'csv';
  static final pageName = 'CustomScrollView';

  @override
  Widget build(BuildContext context) {
    final title = 'Floating App Bar';
    return Scaffold(
      body: CustomScrollView(
        slivers: <Widget>[
          SliverAppBar(
            title: Text(title),
            floating: true,
            flexibleSpace: Placeholder(),
            expandedHeight: 200,
          ),
          SliverList(
            delegate: SliverChildBuilderDelegate(
              (context, index) => ListTile(
                    title: Text('Item #$index'),
                  ),
              childCount: 1000,
            ),
          ),
        ],
      ),
    );
  }
}
