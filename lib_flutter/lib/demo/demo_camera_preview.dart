import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:io';
import 'package:camera/camera.dart';
import 'package:lib_flutter/utils/Loger.dart';
import 'package:path/path.dart' show join;
import 'package:path_provider/path_provider.dart';

class DemoTakePictureApp extends StatefulWidget {
  static final routeName = 'takePicture';
  static final pageName = 'TakePictureApp';

  @override
  State<StatefulWidget> createState() => TakePictureScreenState();
}

class TakePictureScreenState extends State<DemoTakePictureApp> {
  CameraController _controller;
  Future<void> _initializeControllerFuture;

  void fetchCamera() async {
    logd('-->fetchCamera()');
    final cameraList = await availableCameras();
    CameraDescription camera = cameraList.first;
    logd('<--fetchCamera(), camera=$camera');

    setState(() {
      _controller = CameraController(camera, ResolutionPreset.high);
      _initializeControllerFuture = _controller.initialize();
    });
  }

  @override
  void initState() {
    super.initState();
    fetchCamera();
//    _controller = CameraController(widget.camera, ResolutionPreset.medium);
//    _initializeControllerFuture = _controller.initialize();
  }

  Widget _getBodyWidget() {
    logd('-->getBodyWidget(), camera controller=$_controller');
    if (_controller == null || _initializeControllerFuture == null) {
      return Center(
        child: CircularProgressIndicator(),
      );
    } else {
      return FutureBuilder(
          future: _initializeControllerFuture,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              return CameraPreview(_controller);
            } else {
              return Center(
                child: CircularProgressIndicator(),
              );
            }
          });
    }
  }

  @override
  void dispose() {
    super.dispose();
    _controller.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Take a picture'),
      ),
      body: _getBodyWidget(),
      floatingActionButton: FloatingActionButton(
          child: Icon(Icons.camera_alt),
          onPressed: () async {
            try {
              await _initializeControllerFuture;
              final path = join((await getTemporaryDirectory()).path,
                  '${DateTime.now()}.png');

              await _controller.takePicture(path);

              Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (context) => DisplayPictureScreen(path)));
            } catch (e) {
              logd(e);
            }
          }),
    );
  }
}

class DisplayPictureScreen extends StatelessWidget {
  final String imgPath;

  DisplayPictureScreen(this.imgPath);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Display Picture'),
      ),
      body: Image.file(File(imgPath)),
    );
  }
}
