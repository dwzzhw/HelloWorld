import 'package:flutter/material.dart';

class DemoListItemClickApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Sample App',
      theme: new ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: new SampleAppPage(),
    );
  }
}

class SampleAppPage extends StatefulWidget {
  SampleAppPage({Key key}) : super(key: key);

  @override
  _SampleAppPageState createState() => new _SampleAppPageState();
}

class _SampleAppPageState extends State<SampleAppPage> {
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("Sample App"),
      ),
      body: new ListView(children: _getListData()),
//      body: new ListView(
//        children: <Widget>[
//          new Text('This widget has not observed any lifecycle changes 1'),
//          new Text('Row Two'),
//          new Text('Row Three'),
//          new Text('Row Four'),
//        ],
//      ),
    );
  }

  _getListData() {
    List<Widget> widgets = [];
    for (int i = 0; i < 100; i++) {
      widgets.add(new GestureDetector(
        child: new Padding(
            padding: new EdgeInsets.all(10.0), child: new Text("Row $i")),
        onTap: () {
          print('Row[$i] tapped');
          debugPrint('debug-Row[$i] tapped');
        },
      ));
    }
    return widgets;
  }
}
