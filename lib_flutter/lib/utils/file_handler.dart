import 'dart:io';

import 'package:lib_flutter/utils/Loger.dart';
import 'package:path_provider/path_provider.dart';

class FileHandler {
  static const String TAG = 'FileHandler';

  Future<String> get _docDirPath async {
    final directory = await getApplicationDocumentsDirectory();
    return directory.path;
  }

  Future<File> getDocFile(String filePath) async {
    final path = await _docDirPath;
    File resultFile = File('$path/$filePath');
    logd(TAG,
        '-->getDocFile(), target path=$filePath, result path=${resultFile?.path}');
    return resultFile;
  }

  Future<String> get _cacheDirPath async {
    final directory = await getTemporaryDirectory();
    return directory.path;
  }

  Future<File> getCacheFile(String filePath) async {
    final path = await _cacheDirPath;
    File resultFile = File('$path/$filePath');
    logd(TAG,
        '-->getCacheFile(), target path=$filePath, result path=${resultFile?.path}');
    return resultFile;
  }

  Future<String> get _externalDirPath async {
    final directory = await getExternalStorageDirectory();

    return directory.path;
  }

  Future<File> getExternalFile(String filePath) async {
    final path = await _externalDirPath;
    File resultFile = File('$path/$filePath');
    logd(TAG,
        '-->getExternalFile(), target path=$filePath, result path=${resultFile?.path}');
    return resultFile;
  }
}
