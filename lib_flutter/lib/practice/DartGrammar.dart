import 'package:lib_flutter/utils/Loger.dart';

class DartGrammarTest {
  Future doFutureTest() {
    logd('-->doFutureTest()');
    Future future1 = new Future(() => logd('future1'));
    Future future2 = new Future(() {
      logd('future2');
    });
    future2.then((_) {
      logd('then2');
    }).whenComplete(() {
      logd('when 2 complete');
    }).catchError((_) {
      logd('catch2Error');
    });
    future1.then((_) {
      logd('then1');
    }).whenComplete(() {
      logd('when 1 complete');
    }).catchError((_) {
      logd('catch1Error');
    });
  }
}
