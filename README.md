#### 插件说明：

原生安卓拍照与相册获取图片传输给flutter使用



#### 使用注意事项：

AndroidManifest.xml中添加如下权限：

```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```



#### flutter中使用：

##### 导包：

```dart
import 'package:android_picture_plugin/android_picture_plugin.dart';
```



##### 使用示例：

```dart
// 打开相机
await FlutterPicturePlugin.openCamera();
    
// 打开相册
await FlutterPicturePlugin.openAlbum();
```
