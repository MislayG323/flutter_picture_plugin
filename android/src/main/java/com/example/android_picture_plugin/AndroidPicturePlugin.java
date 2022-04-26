package com.example.android_to_flutter_picture_plugin;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

public class AndroidPicturePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// 将 Flutter 与原生 Android 通信的 MethodChannel
  ///
  /// 这个本地引用用于向 Flutter Engine 注册插件，并在 Flutter Engine 与 Activity 分离时取消注册

  private static final String PLUGIN_NAME = "flutter_plugin_demo01";

  private MethodChannel channel;
  private Application mApplication;
  String[] mPermissionList = new String[]{
          Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.READ_EXTERNAL_STORAGE};
  public static final int REQUEST_PICK_IMAGE = 11101;
  String path;
  private WeakReference<Activity> mActivity;

  //此处是新的插件加载注册方式
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), PLUGIN_NAME);
    mApplication = (Application) flutterPluginBinding.getApplicationContext();
    channel.setMethodCallHandler(this);
  }

  /// 请求权限集
  /*@Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case 100:
        boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
        if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
          getImage();
        } else {
          Toast.makeText(this, "请设置必要权限", Toast.LENGTH_SHORT).show();
        }

        break;
    }
  }*/

  private void getImage() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      mActivity.get().startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
              REQUEST_PICK_IMAGE);
    } else {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("image/*");
      mActivity.get().startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "openCamera":
        openCamera(call, result);
        break;
      case "openAlbum":
        openAlbum(call, result);
        break;
      default:
        result.notImplemented();
        break;
    }
  }

  void openCamera(@NonNull MethodCall call, @NonNull Result result) {
    Activity activity = mActivity.get();
    path = (String) call.arguments;
    System.out.println("path >>> " + path);
    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
      activity.startActivityForResult(takePhotoIntent, 1);
    } else {
      System.out.println("相机打开失败");
    }
    result.success("打开相机成功!");
  }

  void openAlbum(@NonNull MethodCall call, @NonNull Result result) {
    Activity activity = mActivity.get();
    ActivityCompat.requestPermissions(activity, mPermissionList, 100);
    result.success("打开相册成功!");
  }

  /*@Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1 && resultCode == RESULT_OK) {
      System.out.println("if (requestCode == 1 && resultCode == RESULT_OK) 进来了");
      // TODO 这里get()被null调用
      Bitmap bitmap = (Bitmap) data.getExtras().get("data");
      FileOutputStream ops;
      try {
        ops = new FileOutputStream(path);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      System.out.println("拍照成功");
      channel.invokeMethod("callback_photo", path);
    }
    System.out.println("if (requestCode == 1 && resultCode == RESULT_OK) 没进来了");
    if (resultCode == Activity.RESULT_OK) {
      switch (requestCode) {
        case REQUEST_PICK_IMAGE:
          if (data != null) {
            path = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
          } else {
            Toast.makeText(this, "图片损坏，请重新选择", Toast.LENGTH_SHORT).show();
          }
          break;
      }
      channel.invokeMethod("callback_photo", path);
    }
  }*/

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.mActivity = new WeakReference<>(binding.getActivity());
    binding.addRequestPermissionsResultListener(new PluginRegistry.RequestPermissionsResultListener() {
      @Override
      public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
          case 100:
            boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (grantResults.length > 0 && writeExternalStorage && readExternalStorage) {
              getImage();
            } else {
              Toast.makeText(mActivity.get(), "请设置必要权限", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        return false;
      }
    });
    binding.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
      @Override
      public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == -1) {
          Bitmap bitmap = (Bitmap) data.getExtras().get("data");
          FileOutputStream ops;
          try {
            ops = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
          channel.invokeMethod("callback_photo", path);
        }
        if (resultCode == Activity.RESULT_OK) {
          switch (requestCode) {
            case REQUEST_PICK_IMAGE:
              if (data != null) {
                path = RealPathFromUriUtils.getRealPathFromUri(mActivity.get(), data.getData());
              } else {
                Toast.makeText(mActivity.get(), "图片损坏，请重新选择", Toast.LENGTH_SHORT).show();
              }
              break;
          }
          channel.invokeMethod("callback_photo", path);
        }
        return false;
      }
    });
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    mActivity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {

  }

  //此处是旧的插件加载注册方式
  public static void registerWith(PluginRegistry.Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), PLUGIN_NAME);
    channel.setMethodCallHandler(new com.example.android_to_flutter_picture_plugin.AndroidPicturePlugin().initPlugin(channel, registrar));
  }

  public com.example.android_to_flutter_picture_plugin.AndroidPicturePlugin initPlugin(MethodChannel methodChannel, PluginRegistry.Registrar registrar) {
    channel = methodChannel;
    mApplication = (Application) registrar.context().getApplicationContext();
    mActivity = new WeakReference<>(registrar.activity());
    return this;
  }

}
