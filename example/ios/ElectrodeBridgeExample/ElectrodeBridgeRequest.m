//
//  ElectrodeBridgeRequest.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/4/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "ElectrodeBridgeRequest.h"

#define kElectrodeBridgeRequestTimeout 5

@interface ElectrodeBridgeRequest ()

@property (nonatomic, copy, readwrite) NSString *name;
@property (nonatomic, strong, readwrite) NSDictionary *data;
@property (nonatomic, assign, readwrite) EBDispatchMode dispatchMode;
@property (nonatomic, assign, readwrite) NSInteger timeout;

@end


@implementation ElectrodeBridgeRequest

- (instancetype)initWithName:(NSString *)name data:(NSDictionary *)data mode:(EBDispatchMode)mode {
  
  self = [super init];
  if (self) {
    self.name = name;
    self.data = data;
    self.dispatchMode = mode;
    self.timeout = kElectrodeBridgeRequestTimeout;
  }
  
  return self;
}

- (instancetype)initWithName:(NSString *)name data:(NSDictionary *)data mode:(EBDispatchMode)mode timeout:(NSInteger)timeout {
  
  self = [self initWithName:name data:data mode:mode];
  if (self) {
    self.timeout = timeout;
  }
  
  return self;
}
@end
