import 'package:flutter/services.dart';
import 'package:lib_flutter/utils/Loger.dart';

class NativeChannelManager {
  static const String TAG = 'NativeChannelManager';
  static const METHOD_CHANNEL_NAME = 'sports_native_flutter_channel';

  static const METHOD_F2N_SAY_HELLO = 'native_to_flutter_say_hello';
  static const METHOD_N2F_SAY_HELLO = 'native_to_flutter_say_hello';

  static const METHOD_F2N_SHOW_COMMENT_PANEL =
      'flutter_to_native_show_comment_panel';
  static const METHOD_N2F_SEND_COMMENT = 'native_to_flutter_send_comment';

  MethodChannel methodChannel;

  NativeChannelManager() {
    methodChannel = MethodChannel(METHOD_CHANNEL_NAME);
    _initMethodChannel();
  }

  void _initMethodChannel() {
    methodChannel.setMethodCallHandler(_handleNativeMethodCall);
  }

  Future<String> _handleNativeMethodCall(MethodCall call) async {
    String respMsg = 'Unknown msg';
    Loger.d(TAG,
        '-->_handleNativeMethodCall(), method=${call.method}, args=${call.arguments}');

    if (METHOD_N2F_SAY_HELLO == call.method) {
      respMsg = 'Nice to meet you too, ${call.arguments}';
    } else if (METHOD_N2F_SEND_COMMENT == call.method) {
      respMsg = 'Yes, it is time to send comment: ${call.arguments}';
    }
    return respMsg;
  }

  Future<String> callNativeMethod(String methodName,
      [dynamic arguments]) async {
    Loger.d(TAG,
        '-->callNativeMethod(), methodName=$methodName, params=$arguments');

    String respMsp;
    try {
      respMsp = await methodChannel.invokeMethod(methodName, arguments);
    } on PlatformException catch (e) {
      respMsp = 'Fail to call native method: ${e.message}';
    } catch (e) {
      respMsp = 'Unexpected msg: $e';
    }
    return respMsp;
  }

  void showNativeCommentPanel(){
    callNativeMethod(METHOD_F2N_SHOW_COMMENT_PANEL);
  }
}
