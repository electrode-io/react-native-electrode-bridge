//
//  ElectrodeEventDispatcher.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/19/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "ElectrodeEventDispatcher.h"
#import "ElectrodeEventRegistrar.h"

@implementation ElectrodeEventDispatcher

- (instancetype)init
{
  self = [super init];
  if (self)
  {
    _eventRegistrar = [[ElectrodeEventRegistrar alloc] init];
  }
  
  return self;
}

- (void)dispatchEvent:(NSString *)event id:(NSString *)eventID data:(NSDictionary *)data
{
  for (id<ElectrodeEventListener> eventListener in [_eventRegistrar getEventListnersForName:event])
  {
    dispatch_async(dispatch_get_main_queue(), ^{
      [eventListener onEvent:data];
    });
  }
}
@end
