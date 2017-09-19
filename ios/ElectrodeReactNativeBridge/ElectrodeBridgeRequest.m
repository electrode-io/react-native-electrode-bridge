//
//  ElectrodeBridgeRequest.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeRequest.h"
#import "ElectrodeLogger.h"

const int kElectrodeBridgeRequestDefaultTimeOut = 5000;
const int kElectrodeBridgeRequestNoTimeOut = -1;

@interface ElectrodeBridgeRequest ()

@property(nonatomic, assign) int timeoutMs;
@property(nonatomic, assign) BOOL isJsInitiated;

@end

@implementation ElectrodeBridgeRequest

+ (nullable instancetype)createRequestWithData:(NSDictionary *)data {
  if ([super isValidFromData:data withType:ElectrodeMessageTypeRequest]) {
    return [[self alloc] initWithData:data];
  }

  ERNDebug(@"cannot create class ElectrodeBridgeRequest with data");
  return nil;
}

- (instancetype)initWithData:(NSDictionary *)data {
  if (self = [super initWithData:data]) {
    _timeoutMs = kElectrodeBridgeRequestNoTimeOut;
    _isJsInitiated = YES;
  }
  return self;
}

- (instancetype)initWithName:(NSString *)name data:(id)data {
  if (self = [super initWithName:name
                            type:ElectrodeMessageTypeRequest
                            data:data]) {
    _timeoutMs = kElectrodeBridgeRequestDefaultTimeOut;
    _isJsInitiated = NO;
  }
  return self;
}

- (NSString *)description {
  return [NSString stringWithFormat:@"%@, timeOut:%d, isJsInitiated:%d",
                                    [super description], self.timeoutMs,
                                    self.isJsInitiated];
}

- (id)copyWithZone:(nullable NSZone *)zone {
  return self;
}

@end
