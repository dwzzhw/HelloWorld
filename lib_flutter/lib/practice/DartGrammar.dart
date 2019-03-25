import 'package:lib_flutter/utils/Loger.dart';

class DartGrammarTest {
  Future doFutureTest() {
    printLog('-->doFutureTest()');
    Future future1 = new Future(() => printLog('future1'));
    Future future2 = new Future(() {
      printLog('future2');
    });
    future2.then((_) {
      printLog('then2');
    }).whenComplete(() {
      printLog('when 2 complete');
    }).catchError((_) {
      printLog('catch2Error');
    });
    future1.then((_) {
      printLog('then1');
    }).whenComplete(() {
      printLog('when 1 complete');
    }).catchError((_) {
      printLog('catch1Error');
    });
  }
}
