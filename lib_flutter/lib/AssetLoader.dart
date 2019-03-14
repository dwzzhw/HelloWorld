import 'package:flutter/services.dart'; // show rootBundle
import 'dart:async' show Future;
import 'package:flutter/material.dart';

Future<String> loadAsset() async {
  return await rootBundle.loadString('assets/config.json');
}

class TestClass extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    rootBundle.load('d');

    DefaultAssetBundle.of(context).load('d');

    return null;
  }
}
