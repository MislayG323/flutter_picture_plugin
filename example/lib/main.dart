
import 'dart:io';

import 'package:android_picture_plugin/android_picture_plugin.dart';
import 'package:flutter/material.dart';

import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  /// 照片当前状态
  String _picturesShowState = '未打开';
  /// 照片的路径
  String _pickPath = "";

  @override
  void initState() {
    super.initState();
    /// 注册通道回调
    FlutterPicturePlugin.channel.setMethodCallHandler((call) => callBack(call));
  }

  /// 更新页面图片
  upDatePicture(MethodCall call) {
    if (!mounted) return;
    setState(() {
      _pickPath = call.arguments;
    });
  }

  /// 通道回调
  callBack(MethodCall call) {
    switch (call.method) {
      case "callback_photo":
        upDatePicture(call);
        break;
    }
  }

  /// 请求原生相机
  openCamera() async {
    String openCameraState;
    try {
      openCameraState = await FlutterPicturePlugin.openCamera() ?? '未检测到状态';
    } on PlatformException {
      openCameraState = '打开相机失败！';
    }

    if (!mounted) return;

    setState(() {
      _picturesShowState = openCameraState;
    });
  }

  /// 请求原生相册
  openAlbum() async {
    String openAlbumState;
    try {
      openAlbumState = await FlutterPicturePlugin.openAlbum() ?? 'Unknown openCameraState';
    } on PlatformException {
      openAlbumState = '打开相册失败！';
    }
    if (!mounted) return;

    setState(() {
      _picturesShowState = openAlbumState;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter插件 调用安卓原相机、相册'),
        ),
        body: Center(
          child: Column(
            children: [
              Container(
                child: _pickPath == "" ?Container():Container(
                  width: 135,
                  height: 135,
                  decoration: BoxDecoration(
                    borderRadius: const BorderRadius.all(Radius.circular(100)),
                    image: DecorationImage(
                      image: FileImage(File(_pickPath)),
                      fit: BoxFit.fill,
                    ),
                  ),
                ),
              ),
              Text('相机状态为: $_picturesShowState\n'),
              ElevatedButton(
                onPressed: openCamera,
                child: const Text('打开相机'),
              ),
              ElevatedButton(
                onPressed: openAlbum,
                child: const Text('打开相册'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
