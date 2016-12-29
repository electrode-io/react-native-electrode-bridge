//
//  ElectrodeBridge.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/12/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "ElectrodeBridge.h"
#import "ElectrodeEventDispatcher.h"
#import "ElectrodeEventRegistrar.h"
#import "ElectrodeBridgeEvent.h"
#import "RCTLog.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"
#import "ElectrodeBridgeHolder.h"

NSString * const EBBridgeEvent = @"electrode.bridge.event";
NSString * const EBBridgeRequest = @"electrode.bridge.request";
NSString * const EBBridgeResponse = @"electrode.bridge.response";
NSString * const EBBridgeError = @"error";
NSString * const EBBridgeErrorCode = @"code";
NSString * const EBBridgeErrorMessage = @"message";
NSString * const EBBridgeMsgData = @"data";
NSString * const EBBridgeMsgName = @"name";
NSString * const EBBridgeMsgID = @"id";
NSString * const EBBridgeRequestID = @"requestId";
NSString * const EBBridgeUnknownError = @"EUNKNOWN";

/*
 private static final String BRIDGE_EVENT = "electrode.bridge.event";
 private static final String BRIDE_REQUEST = "electrode.bridge.request";
 private static final String BRIDGE_RESPONSE = "electrode.bridge.response";
 private static final String BRIDGE_RESPONSE_ERROR = "error";
 private static final String BRDIGE_RESPONSE_ERROR_CODE = "code";
 private static final String BRIDGE_RESPONSE_ERROR_MESSAGE = "message";
 private static final String BRIDGE_MSG_DATA = "data";
 private static final String BRIDGE_MSG_NAME = "name";
 private static final String BRIDGE_MSG_ID = "id";
 private static final String BRIDGE_REQUEST_ID = "requestId";
 private static final String UNKNOWN_ERROR_CODE = "EUNKNOWN";
 */

@interface ElectrodeBridge ()

@property (nonatomic, assign) BOOL usedPromise;
@property (nonatomic, strong) ElectrodeEventDispatcher *eventDispatcher;
@end

@implementation ElectrodeBridge

@synthesize bridge = _bridge;

- (instancetype)init
{
  self = [super init];
  if (self)
  {
    self.eventDispatcher = [[ElectrodeEventDispatcher alloc] init];
    [ElectrodeBridgeHolder sharedInstance].bridge = self;
  }
  return self;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(dispatchRequest:(NSString *)name id:(NSString *)id data:(NSDictionary *)data resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
  RCTLogInfo(@"TODO: Implement dispatch request: %@ %@ %@", name, id, data);
  
  if (!_usedPromise)
  {
    resolve(@{@"Received": @"yep"});
  }
  else
  {
    NSError *error = [NSError errorWithDomain:@"ElectrodeBridge" code:41 userInfo:@{@"Failed":@"no idea why"}];
    reject(@"41", @"Message failed", error);
  }
  
  self.usedPromise = !self.usedPromise;
}

RCT_EXPORT_METHOD(dispatchEvent:(NSString *)event id:(NSString *)id data:(NSDictionary *)data)
{
  RCTLogInfo(@"onEvent[name:%@ id:%@] %@", event, id, data);
  
  if ([event isEqualToString:EBBridgeResponse])
  {
    NSString *parentRequestID = [data objectForKey:EBBridgeRequestID];
    RCTLogInfo(@"Received response [id:%@", parentRequestID);

    
  }
  else
  {
    [_eventDispatcher dispatchEvent:event id:id data:data];
  }
}

/*
if (name.equals(BRIDGE_RESPONSE)) {
  String parentRequestId = data.getString(BRIDGE_REQUEST_ID);
  Log.d(TAG, String.format("Received response [id:%s]", parentRequestId));
  Promise promise = pendingPromiseByRequestId.remove(parentRequestId);
  if (data.hasKey(BRIDGE_RESPONSE_ERROR)) {
    String errorMessage = data
    .getMap(BRIDGE_RESPONSE_ERROR)
    .getString(BRIDGE_RESPONSE_ERROR_MESSAGE);
    
    String errorCode = UNKNOWN_ERROR_CODE;
    if (data.getMap(BRIDGE_RESPONSE_ERROR)
        .hasKey(BRDIGE_RESPONSE_ERROR_CODE)) {
      errorCode = data
      .getMap(BRIDGE_RESPONSE_ERROR)
      .getString(BRDIGE_RESPONSE_ERROR_CODE);
    }
    promise.reject(errorCode, errorMessage);
  } else if (data.hasKey(BRIDGE_MSG_DATA)) {
    promise.resolve(data.getMap(BRIDGE_MSG_DATA));
  } else {
    promise.reject(new UnsupportedOperationException());
  }
} else {
  mEventDispatcher.dispatchEvent(id, name, data);
}*/

- (NSString *)getUUID
{
  return [[NSUUID UUID] UUIDString];
}

- (void)emitEvent:(ElectrodeBridgeEvent *)event id:(NSString *)id
{

  RCTLogInfo(@"Emitting event[name:%@ id:%@", event.name, id);
  
  if (event.dispatchMode == JS)
  { // Handle JS
    
    // TODO: Update later to get rid of warning
    [self.bridge.eventDispatcher sendAppEventWithName:EBBridgeEvent
                                                 body:@{EBBridgeMsgID: id,
                                                        EBBridgeMsgName: event.name,
                                                        EBBridgeMsgData: event.data}];
  }
  else if (event.dispatchMode == NATIVE)
  { // Handle Native
    
  }
  else
  { // Must be global
    
  }
}

- (ElectrodeEventRegistrar *)eventRegistrar
{
  return self.eventDispatcher.eventRegistrar;
}
@end
