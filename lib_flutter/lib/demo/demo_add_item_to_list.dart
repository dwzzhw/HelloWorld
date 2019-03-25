import 'package:css_colors/css_colors.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:url_launcher/url_launcher.dart';

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
//        child: new Text("Row $i"),
        child: _getRowContent(i),
      ),
      onTap: () {
        setState(() {
          if (i == 0 && widgets.length > 2) {
            launchFlutterHomePage();
          } else if (i % 2 == 0) {
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

  Widget _getRowContent(int rowNum) {
    if (rowNum == 0) {
      return new Text("Jump to flutter home page");
    } else if (rowNum % 2 == 0) {
      return new Text("Row no BG $rowNum");
    } else {
      return new Container(
        child: new Text("Row with BG $rowNum"),
        color: CSSColors.orange,
      );
    }
  }

  void launchFlutterHomePage() {
    printLog('-->launchFlutterHomePage()');
    launch('https://flutterchina.club/');
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
