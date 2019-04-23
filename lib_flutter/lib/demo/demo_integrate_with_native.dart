import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../channel/native_channel_manager.dart';
import '../utils/Loger.dart';

class DemoIntegrateWithNative extends StatefulWidget {
  static final routeName = 'integrate';
  static final pageName = 'IntegrateWithNative';

  @override
  State<StatefulWidget> createState() => IntegrateWithNativeState();
}

class IntegrateWithNativeState extends State<DemoIntegrateWithNative> {
  String _msgFromNative = 'undefined msg';
  NativeChannelManager nativeChannelManager;

  @override
  void initState() {
    super.initState();
    nativeChannelManager = NativeChannelManager();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Integrate with native'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: <Widget>[
            RaisedButton(
              child: Text('Say Hello to native'),
              onPressed: callNativeMethod,
            ),
            Text('Msg from native: $_msgFromNative')
          ],
        ),
      ),
    );
  }

  void callNativeMethod() {
    Future<String> future = nativeChannelManager
        .callNativeMethod(NativeChannelManager.METHOD_F2N_SAY_HELLO);
    future.then((msg) {
      setState(() {
        Loger.d('Integrate test', 'msg=$msg');
        _msgFromNative = msg;
      });
    });
  }
}
