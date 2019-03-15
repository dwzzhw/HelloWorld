import 'package:flutter/material.dart';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:english_words/english_words.dart';
import 'package:lib_flutter/demo/demo_add_item_to_list.dart';
import 'package:lib_flutter/demo/demo_lake.dart';
import 'package:lib_flutter/demo/demo_lifecycle_watcher.dart';
import 'package:lib_flutter/demo/demo_list_item_click.dart';
import 'package:lib_flutter/demo/demo_load_json_from_net.dart';
import 'package:lib_flutter/demo/demo_random_words.dart';
import 'package:lib_flutter/demo/demo_shopping_list.dart';
import 'package:lib_flutter/demo/demo_signature_painter.dart';
import 'package:lib_flutter/demo/demo_simple_count.dart';
import 'demo/demo_counter_display.dart';

//void main() => runApp(new DemoShoppingListApp());
//void main() => runApp(new DemoCounterDisplayApp());
//void main() => runApp(new DemoRandomWordsApp());
//void main() => runApp(new DemoSimpleCountApp());
//void main() => runApp(new MaterialApp(home: new PainterApp()));
//void main() => runApp(new DemoLoadJsonFromNetApp());
//void main() => runApp(new DemoLifecycleWatcherApp());
//void main() => runApp(new DemoListItemClickApp());
//void main() {
//  print("dwz Hello 2019-0314 23:59, enter build function, route is " + window.defaultRouteName);
//
//  runApp(new DemoLakeApp());
//}
void main() => runApp(new MaterialApp(
      home:
//    new LifecycleWatcher(),
          new DemoAddItemToList(),
    ));