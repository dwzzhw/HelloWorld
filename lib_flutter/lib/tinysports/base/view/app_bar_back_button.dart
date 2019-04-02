import 'package:flutter/material.dart';

class AppBarBackButton extends StatelessWidget {
  final VoidCallback onBackPressListener;

  AppBarBackButton({this.onBackPressListener});

  @override
  Widget build(BuildContext context) {
    return IconButton(
      icon: Icon(Icons.chevron_left),
      onPressed: () {
        if (onBackPressListener != null) {
          onBackPressListener();
        } else {
          Navigator.pop(context);
        }
      },
    );
  }
}
