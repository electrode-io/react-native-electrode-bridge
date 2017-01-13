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
#import "ElectrodeRequestDispatcher.h"
#import "ElectrodeBridgeRequest.h"

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

@interface ElectrodeBridge ()

@property (nonatomic, assign) BOOL usedPromise;
@property (nonatomic, strong) ElectrodeEventDispatcher *eventDispatcher;
@property (nonatomic, strong) ElectrodeRequestDispatcher *requestDispatcher;
@end

@implementation ElectrodeBridge

@synthesize bridge = _bridge;

- (instancetype)init
{
  self = [super init];
  if (self)
  {
    self.eventDispatcher = [[ElectrodeEventDispatcher alloc] init];
    self.requestDispatcher = [[ElectrodeRequestDispatcher alloc] init];
    [ElectrodeBridgeHolder sharedInstance].bridge = self;
  }
  return self;
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(dispatchRequest:(NSString *)name id:(NSString *)id
                  data:(NSDictionary *)data
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  RCTLogInfo(@"dispatchRequest[name:%@ id:%@]", name, id);

  [self.requestDispatcher dispatchRequest:name id:id data:data completion:
   ^(NSDictionary *data, NSError *error)
  {
    if (!error)
    {
      resolve(data);
    }
    else
    {
      reject([NSString stringWithFormat:@"%zd", error.code], error.localizedDescription, error);
    }
  }];
}

RCT_EXPORT_METHOD(dispatchEvent:(NSString *)event
                  id:(NSString *)id
                  data:(NSDictionary *)data)
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

- (void)emitEvent:(ElectrodeBridgeEvent *)event
{

  NSString *eventID = [self getUUID];
  RCTLogInfo(@"Emitting event[name:%@ id:%@", event.name, eventID);
  
  if (event.dispatchMode == JS || event.dispatchMode == GLOBAL)
  { // Handle JS

    NSDictionary *body = nil;
    if (event.data)
    {
      body = @{EBBridgeMsgID: eventID,
               EBBridgeMsgName: event.name,
               EBBridgeMsgData: event.data};
    }
    else
    {
      body = @{EBBridgeMsgID: eventID,
               EBBridgeMsgName: event.name};
    }

    
    // TODO: Update later to get rid of warning
    [self.bridge.eventDispatcher sendAppEventWithName:EBBridgeEvent
                                                 body:body];
  }
  
  if (event.dispatchMode == NATIVE || event.dispatchMode == GLOBAL)
  { // Handle Native dispatches
    [_eventDispatcher dispatchEvent:event.name id:eventID data:event.data];
  }
}

- (void)sendRequest:(ElectrodeBridgeRequest *)request
 completionListener:(id<ElectrodeRequestCompletionListener>)listener
{
  NSString *requestID = [self getUUID];

  RCTLogInfo(@"Sending request[name:%@ id:%@", request.name, requestID);
  
  // Dispatch to JS or Native depending which was selected
  if (request.dispatchMode == JS)
  {
    NSDictionary *body = nil;
    if (request.data)
    {
      body = @{EBBridgeMsgID: requestID,
               EBBridgeMsgName: request.name,
               EBBridgeMsgData: request.data};
    }
    else
    {
      body = @{EBBridgeMsgID: requestID,
               EBBridgeMsgName: request.name};
    }
    
    // TODO: Update later to get rid of warning
    [self.bridge.eventDispatcher sendAppEventWithName:EBBridgeRequest
                                                 body:body];
  }
  else
  {
    [_requestDispatcher dispatchRequest:request.name id:requestID data:request.data completion:^(NSDictionary *data, NSError *error) {
      if (!error)
      {
        [listener onSuccess:data];
      }
      else
      {
        [listener onError:error.domain message:error.localizedDescription];
      }
    }];
  }
}

- (ElectrodeEventRegistrar *)eventRegistrar
{
  return self.eventDispatcher.eventRegistrar;
}

- (ElectrodeRequestRegistrar *)requestRegistrar
{
  return self.requestDispatcher.requestRegistrar;
}
@end
