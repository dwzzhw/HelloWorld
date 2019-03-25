import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lib_flutter/utils/Loger.dart';

class DemoIntegrateWithNative extends StatefulWidget {
  static final routeName = 'integrate';
  static final pageName = 'IntegrateWithNative';

  static const METHOD_CHANNEL_NAME = 'demo.integrate/sayhello';
  static const METHOD_TO_NATIVE = 'flutterSayHello';
  static const METHOD_FROM_ANDROID = 'androidSayHello';

  @override
  State<StatefulWidget> createState() => IntegrateWithNativeState();
}

class IntegrateWithNativeState extends State<DemoIntegrateWithNative> {
  static const methodChannel =
      const MethodChannel('${DemoIntegrateWithNative.METHOD_CHANNEL_NAME}');
  String _msgFromNative = 'undefined msg';

  @override
  void initState() {
    super.initState();

    methodChannel.setMethodCallHandler(_handleNativeCall);
  }

  Future<String> _handleNativeCall(MethodCall call) async {
    String msg = 'Unknown msg';
    printLog(
        '-->_handleNativeCall(), method=${call.method}, args=${call.arguments}');
    if (DemoIntegrateWithNative.METHOD_FROM_ANDROID == call.method) {
      printLog('-->receive message from android');
      msg = 'Nice to meet you too, ${call.arguments}';
    }
    return getMsg(msg);
  }

  String getMsg(String msg) {
    return msg;
  }

  Future<void> _getMsgFromNative() async {
    String msg;
    printLog('-->_getMsgFromNative');
    try {
      msg = await methodChannel
          .invokeMethod('${DemoIntegrateWithNative.METHOD_TO_NATIVE}');
    } on PlatformException catch (e) {
      msg = 'Fail to receive msg from native: ${e.message}';
    } catch (e) {
      msg = 'Unexpected msg: $e';
    }
    printLog('<--_getMsgFromNative, msg=$msg');

    setState(() {
      _msgFromNative = msg;
    });
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
              onPressed: _getMsgFromNative,
            ),
            Text('Msg from native: $_msgFromNative')
          ],
        ),
      ),
    );
  }
}
