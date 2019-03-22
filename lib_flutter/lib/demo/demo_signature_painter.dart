import 'package:flutter/material.dart';

//dwz test, this demo not work yet
class SignaturePainter extends CustomPainter {
  final List<Offset> points;

  SignaturePainter(this.points);

  @override
  void paint(Canvas canvas, Size size) {
    var paint = new Paint()
      ..color = Colors.black
      ..strokeCap = StrokeCap.round
      ..strokeWidth = 5.0;
    for (int i = 0; i < points.length - 1; i++) {
      if (points[i] != null && points[i + 1] != null) {
        canvas.drawLine(points[i], points[i + 1], paint);
      }
    }
  }

  @override
  bool shouldRepaint(SignaturePainter oldDelegate) {
    return oldDelegate.points != points;
  }
}

class Signature extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => new SignatureState();
}

class SignatureState extends State<Signature> {
  List<Offset> _points = <Offset>[];

  void _handleOnTap(){
    debugPrint('OnTag detected');
  }
  @override
  Widget build(BuildContext context) {
    return new GestureDetector(
      onPanUpdate: (DragUpdateDetails details) {
        setState(() {
          RenderBox referenceBox = context.findRenderObject();
          Offset localPosition =
              referenceBox.globalToLocal(details.globalPosition);
          _points = new List.from(_points)..add(localPosition);
        });
      },
      onPanEnd: (DragEndDetails details) => _points.addAll(null),
      child: new CustomPaint(painter: new SignaturePainter(_points)),
      onTap: _handleOnTap,
    );
  }
}

class PainterApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    debugPrint('Build PainterApp');
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Signature Painter1'),
      ),
      body: new Signature(),
    );
  }
}

class SignaturePainterApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new PainterApp(),
    );
  }
}
