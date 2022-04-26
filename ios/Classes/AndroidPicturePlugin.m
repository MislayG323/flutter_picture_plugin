#import "AndroidPicturePlugin.h"
#if __has_include(<android_picture_plugin/android_picture_plugin-Swift.h>)
#import <android_picture_plugin/android_picture_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "android_picture_plugin-Swift.h"
#endif

@implementation AndroidPicturePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAndroidPicturePlugin registerWithRegistrar:registrar];
}
@end
