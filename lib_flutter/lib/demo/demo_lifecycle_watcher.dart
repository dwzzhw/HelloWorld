import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class DemoLifecycleWatcher extends StatefulWidget {
  static final routeName = 'life_cycle_watch';
  static final pageName = 'LifecycleWatcher';
  @override
  State<StatefulWidget> createState() {
    return new _DemoLifecycleWatcherState();
  }
}

class _DemoLifecycleWatcherState extends State<DemoLifecycleWatcher>
    with WidgetsBindingObserver {
  AppLifecycleState _lastLifecycleState;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    setState(() {
      _lastLifecycleState = state;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_lastLifecycleState == null) {
//      return new Row(
//        mainAxisAlignment: MainAxisAlignment.center,
//        children: <Widget>[
//          new Text('Row One.'),
//          new Text('Row Two'),
//          new Text('Row Three'),
//          new Text('Row Four'),
//        ],
//      );
//      return new ListView(
//        children: <Widget>[
//          new Text('This widget has not observed any lifecycle changes'),
//          new Text('Row Two'),
//          new Text('Row Three'),
//          new Text('Row Four'),
//        ],
//        scrollDirection: Axis.vertical,
//      );
      return new Scaffold(
        appBar: new AppBar(
          title: new Text("Sample App"),
        ),
        body: new ListView(
            children: <Widget>[
              new Text('This widget has not observed any lifecycle changes'),
              new Text('Row Two'),
              new Text('Row Three'),
              new Text('Row Four'),
            ],
          ),
      );
//      return new Center(
//        child: new Text(
//          'This widget has not observed any lifecycle changes.',
//          textDirection: TextDirection.ltr,
//        ),
//      );
    } else {
      return new Center(
        child: new Text(
            'The most recent lifecycle state this widget observed was: $_lastLifecycleState.',
            textDirection: TextDirection.ltr),
      );
    }
  }
}
