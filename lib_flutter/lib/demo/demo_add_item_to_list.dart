import 'package:flutter/material.dart';

class DemoAddItemToList extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return new _AddItemToListState();
  }
}

class _AddItemToListState extends State<DemoAddItemToList> {
  List widgets = [];

  @override
  void initState() {
    super.initState();
    for (int i = 0; i < 10; i++) {
      widgets.add(getRow(i));
    }
  }

  Widget getRow(int i) {
    return new GestureDetector(
      child: new Padding(
        padding: new EdgeInsets.all(10),
        child: new Text("Row $i"),
      ),
      onTap: () {
        setState(() {
          if (i % 2 == 0) {
            widgets.add(getRow(widgets.length));
            debugPrint('Click single row $i, add one more line');
          } else {
            widgets.removeAt(widgets.length - 1);
            debugPrint('Click double row $i, delete last line');
          }
        });
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Add item to list Demo'),
      ),
      body: new ListView.builder(
          itemCount: widgets.length,
          itemBuilder: (BuildContext context, int position) {
            return widgets[position];
          }),
    );
  }
}
