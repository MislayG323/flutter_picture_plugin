
import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class FlutterPicturePlugin {
  /// 通道名字
  static const MethodChannel channel = MethodChannel('flutter_plugin_demo01');

  /// 打开相机通道
  static Future<String?> openCamera() async {
    String path = Directory.systemTemp.parent.path;
    final photoState = await channel.invokeMethod("openCamera", path + "/photo${DateTime.now().toString()}.jpg");
    return photoState;
  }

  /// 打开相册通道
  static Future<String?> openAlbum() async {
    final photoState = await channel.invokeMethod("openAlbum");
    return photoState;
  }
}
