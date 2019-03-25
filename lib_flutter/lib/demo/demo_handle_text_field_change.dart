import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:transparent_image/transparent_image.dart';

class DemoHandleTextFieldChange extends StatefulWidget {
  static final routeName = 'tfc';
  static final pageName = 'HandleTextFieldChange';

  @override
  State<StatefulWidget> createState() {
    return _HandleTextFieldChangeState();
  }
}

class _HandleTextFieldChangeState extends State<DemoHandleTextFieldChange> {
  final _myTextEditingController = TextEditingController();

  void _printTextFieldContent() {
    printLog('Second text field content is [${_myTextEditingController.text}]');
  }

  @override
  void initState() {
    super.initState();
    _myTextEditingController.addListener(_printTextFieldContent);
  }

  @override
  void dispose() {
    _myTextEditingController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Monitor Text Field Change event'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: ListView(
          scrollDirection: Axis.vertical,
          children: <Widget>[
            TextField(
              onChanged: (text) {
                printLog('First text field content: [$text]');
              },
            ),
            TextField(
              controller: _myTextEditingController,
            ),
            FadeInImage.memoryNetwork(
              placeholder: kTransparentImage,
              image: 'https://picsum.photos/250?image=9',
            ),
            CachedNetworkImage(
              placeholder: (context, url) => CircularProgressIndicator(),
              imageUrl: 'https://picsum.photos/250?image=9',
              errorWidget: (context, url, error) => new Icon(Icons.error),
            ),
            InkWell(
              // When the user taps the button, show a snackbar
              onTap: () {
                printLog('Ripple button is clicked');
                _myTextEditingController.text = 'Ripple button is clicked';
                Scaffold.of(context).showSnackBar(SnackBar(
                  content: Text('Tap'),
                ));
              },
              child: Container(
                padding: EdgeInsets.all(20.0),
//                decoration: BoxDecoration(
//                  color: Theme.of(context).buttonColor,
//                  borderRadius: BorderRadius.circular(8),
//                ),
                child: Text('Flat Ripple Button'),
              ),
            )
          ],
        ),
      ),
    );
  }
}
