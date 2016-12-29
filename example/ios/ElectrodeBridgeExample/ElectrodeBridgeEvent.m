//
//  ElectrodeBridgeEvent.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/16/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "ElectrodeBridgeEvent.h"

@interface ElectrodeBridgeEvent ()
@property (nonatomic, copy, readwrite) NSString *name;
@property (nonatomic, strong, readwrite) NSData *data;
@property (nonatomic, assign, readwrite) EBDispatchMode dispatchMode;
@end

@implementation ElectrodeBridgeEvent

- (instancetype)initWithName:(NSString *)name data:(NSData *)data mode:(EBDispatchMode)mode {
  
  self = [super init];
  if (self) {
    self.name = name;
    self.data = data;
    self.dispatchMode = mode;
  }
  
  return self;
}

@end
