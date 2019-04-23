import 'package:flutter/material.dart';
import '../../../utils/Loger.dart';
import '../../../utils/common_utils.dart';
import 'package:video_player/video_player.dart';

class VideoPlayerView extends StatefulWidget {
  final String videoResUrl;
  final String assetsPath;

  VideoPlayerView({this.videoResUrl, this.assetsPath});

  @override
  State<StatefulWidget> createState() =>
      VideoPlayerViewState(videoResUrl, assetsPath);
}

class VideoPlayerViewState extends State<VideoPlayerView> {
  static const String TAG = 'VideoPlayerViewState';
  String videoResUrl;
  String assetsPath;
  VideoPlayerController _controller;
  Future<void> _initializeVideoPlayerFuture;
  double videoTargetWidth;
  double videoTargetHeight;

  VideoPlayerViewState(this.videoResUrl, this.assetsPath);

  @override
  void initState() {
    super.initState();
    Loger.d(TAG,
        '-->initState(), videoResUrl=$videoResUrl, assetsPath=$assetsPath');
    if (!CommonUtils.isEmptyStr(videoResUrl)) {
      _controller = VideoPlayerController.network(videoResUrl);
    } else if (!CommonUtils.isEmptyStr(assetsPath)) {
      _controller = VideoPlayerController.asset(assetsPath);
    }

    if (_controller != null) {
      _initializeVideoPlayerFuture = _controller.initialize();
      _controller.setLooping(true);
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    videoTargetWidth = MediaQuery.of(context).size.width;
    videoTargetHeight = videoTargetWidth * 9 / 16;
    return Scaffold(
      body: FutureBuilder(
        future: _initializeVideoPlayerFuture,
        builder: (BuildContext context, AsyncSnapshot snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
            return Stack(
              alignment: Alignment.center,
              children: <Widget>[
                Container(
                  width: videoTargetWidth,
                  height: videoTargetHeight,
                  child: AspectRatio(
                    aspectRatio: _controller.value.aspectRatio,
                    child: VideoPlayer(_controller),
                  ),
                ),
                Positioned(
                  child: GestureDetector(
                    onTap: _handlePlayBtnClickEvent,
                    child: Container(
                      child: Icon(_controller.value.isPlaying
                          ? Icons.pause
                          : Icons.play_arrow),
                      width: 40,
                      height: 40,
                    ),
                  ),
                  right: 10,
                  bottom: 10,
                ),
              ],
            );
          } else {
            return Container(
              child: Center(child: CircularProgressIndicator()),
              color: Colors.black,
            );
          }
        },
      ),
    );
  }

  void _handlePlayBtnClickEvent() {
    Loger.d(TAG,
        '-->_handlePlayBtnClickEvent(), isPlaying=${_controller.value.isPlaying}');
    setState(() {
      // If the video is playing, pause it.
      if (_controller.value.isPlaying) {
        _controller.pause();
      } else {
        // If the video is paused, play it
        _controller.play();
      }
    });
  }
}
