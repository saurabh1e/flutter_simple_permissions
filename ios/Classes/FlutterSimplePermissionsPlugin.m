#import "FlutterSimplePermissionsPlugin.h"
#import <flutter_simple_permissions/flutter_simple_permissions-Swift.h>

@implementation FlutterSimplePermissionsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterSimplePermissionsPlugin registerWithRegistrar:registrar];
}
@end
