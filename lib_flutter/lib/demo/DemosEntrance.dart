import 'dart:ui';

import 'package:flutter/material.dart';
import '../demo/animation/demo_animation_home.dart';
import '../demo/demo_camera_preview.dart';
import '../demo/demo_custom_scroll_view.dart';
import '../demo/demo_fetch_data_from_net.dart';
import '../demo/demo_handle_text_field_change.dart';
import '../demo/demo_integrate_with_native.dart';
import '../demo/demo_lake.dart';
import '../demo/demo_lifecycle_watcher.dart';
import '../demo/demo_list_item_click.dart';
import '../demo/demo_nested_scroll.dart';
import '../demo/demo_parse_json_background.dart';
import '../demo/demo_random_words.dart';
import '../demo/demo_read_write_local_file.dart';
import '../demo/demo_shopping_list.dart';
import '../demo/demo_sliver_hero_page.dart';
import '../demo/demo_tab_layout.dart';
import '../demo/demo_video_player.dart';
import '../utils/Loger.dart';

//void main() => runApp(new DemoCounterDisplayApp());
//void main() => runApp(new DemoSimpleCountApp());
//void main() => runApp(new MaterialApp(home: new PainterApp()));
//void main() => runApp(new DemoLoadJsonFromNetApp());

class DemosEntrance extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    printLog(
        "DemosEntrance 2019-0319 10:52, route is " + window.defaultRouteName);

    DemoManager demoManager = DemoManager();
    demoManager.initRouteWidgetList();

    return MaterialApp(
      home: demoManager.getTargetWidget(window.defaultRouteName),
    );
  }
}

class DemoManager {
//  Map<String, Widget> mRouteWidgetMap;
  List<DemoItem> demoItemList;

  void initRouteWidgetList() {
    demoItemList = new List();
    demoItemList.add(DemoItem(
        DemoHeroPage.routeName, DemoHeroPage.pageName, DemoHeroPage()));
    demoItemList.add(DemoItem(AnimationDemoHome.routeName,
        AnimationDemoHome.pageName, AnimationDemoHome()));
    demoItemList.add(DemoItem(DemoNestedScrollPage.routeName,
        DemoNestedScrollPage.pageName, DemoNestedScrollPage()));
    demoItemList.add(DemoItem(
        DemoReadWriteFile.routeName,
        DemoReadWriteFile.pageName,
        DemoReadWriteFile(storage: CounterStorage())));
    demoItemList.add(DemoItem(DemoIntegrateWithNative.routeName,
        DemoIntegrateWithNative.pageName, DemoIntegrateWithNative()));
    demoItemList.add(DemoItem(DemoTakePictureApp.routeName,
        DemoTakePictureApp.pageName, DemoTakePictureApp()));
    demoItemList.add(DemoItem(DemoVideoPlayerApp.routeName,
        DemoVideoPlayerApp.pageName, DemoVideoPlayerApp()));
    demoItemList.add(DemoItem(DemoParseJsonBackground.routeName,
        DemoParseJsonBackground.pageName, DemoParseJsonBackground()));
    demoItemList.add(DemoItem(DemoFetchDataFromNet.routeName,
        DemoFetchDataFromNet.pageName, DemoFetchDataFromNet()));
    demoItemList.add(DemoItem(DemoCustomScrollView.routeName,
        DemoCustomScrollView.pageName, DemoCustomScrollView()));
    demoItemList.add(DemoItem(DemoHandleTextFieldChange.routeName,
        DemoHandleTextFieldChange.pageName, DemoHandleTextFieldChange()));
    demoItemList.add(DemoItem(DemoTabBarLayout.routeName,
        DemoTabBarLayout.pageName, DemoTabBarLayout()));
    demoItemList.add(
        DemoItem(DemoLakeApp.routeName, DemoLakeApp.pageName, DemoLakeApp()));
    demoItemList.add(DemoItem(DemoListItemClickApp.routeName,
        DemoListItemClickApp.pageName, DemoListItemClickApp()));
    demoItemList.add(DemoItem(DemoRandomWordsApp.routeName,
        DemoRandomWordsApp.pageName, DemoRandomWordsApp()));
    demoItemList.add(DemoItem(DemoShoppingListApp.routeName,
        DemoShoppingListApp.pageName, DemoShoppingListApp()));
    demoItemList.add(DemoItem(DemoLifecycleWatcher.routeName,
        DemoLifecycleWatcher.pageName, DemoLifecycleWatcher()));

//    mRouteWidgetMap = new HashMap();
//    mRouteWidgetMap[DemoCustomScrollView.routeName] = DemoCustomScrollView();
  }

  Widget getTargetWidget(String routeName) {
    Widget resultWidget = null;
    if (routeName != null) {
      for (int i = 0; i < demoItemList.length; i++) {
        DemoItem demoItem = demoItemList[i];
        if (routeName == demoItem.routeName || routeName == '$i') {
          resultWidget = demoItem.widget;
          break;
        }
      }
    }
    if (resultWidget == null) {
      resultWidget = DemoListPage(demoItemList);
    }

    printLog(
        '-->getTargetWidget(), routeName=$routeName , result widget=$resultWidget');
    return resultWidget;
  }
}

class DemoListPage extends StatelessWidget {
  List<DemoItem> demosList;

  DemoListPage(this.demosList);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Available Demos'),
      ),
      body: ListView.builder(
          itemCount: demosList.length,
          itemBuilder: (context, index) {
            DemoItem item = demosList[index];

            return ListTile(
              title: Text(item.pageName),
              subtitle: Text('route name: ${item.routeName} / $index'),
            );
          }),
    );
  }
}

class DemoItem {
  String routeName;
  String pageName;
  Widget widget;

  DemoItem(this.routeName, this.pageName, this.widget);
}
