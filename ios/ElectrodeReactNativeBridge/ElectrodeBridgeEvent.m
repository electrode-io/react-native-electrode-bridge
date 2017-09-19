//
//  ElectrodeBridgeEvent.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeEvent.h"
#import "ElectrodeBridgeMessage.h"
#import "ElectrodeLogger.h"

@implementation ElectrodeBridgeEvent
+ (nullable instancetype)createEventWithData:(NSDictionary *)data {
  if ([ElectrodeBridgeMessage isValidFromData:data
                                     withType:ElectrodeMessageTypeEvent]) {
    return [[super alloc] initWithData:data];
  }

  ERNDebug(@"%@ : unable to create with data %@",
           [ElectrodeBridgeEvent className], data);
  return nil;
}

- (instancetype)initWithName:(NSString *)name data:(id)data {
  if (self =
          [super initWithName:name type:ElectrodeMessageTypeEvent data:data]) {
    return self;
  }
  ERNDebug(@"%@ : unable to create with data %@",
           [ElectrodeBridgeEvent className], data);
  return nil;
}

+ (NSString *)className {
  return NSStringFromClass(self.class);
}

@end
