//
//  ElectrodeBridge.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/12/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTBridgeModule.h"

extern NSString * const EBBridgeEvent;
extern NSString * const EBBridgeRequest;
extern NSString * const EBBridgeResponse;
extern NSString * const EBBridgeError;
extern NSString * const EBBridgeErrorCode;
extern NSString * const EBBridgeErrorMessage;
extern NSString * const EBBridgeMsgData;
extern NSString * const EBBridgeMsgName;
extern NSString * const EBBridgeMsgID;
extern NSString * const EBBridgeRequestID;
extern NSString * const EBBridgeUnknownError;

typedef NS_ENUM(NSInteger, EBDispatchMode)
{
  JS,
  NATIVE,
  GLOBAL
};

@class ElectrodeBridgeEvent, ElectrodeEventRegistrar;

@interface ElectrodeBridge : NSObject <RCTBridgeModule>

@property (nonatomic, readonly) ElectrodeEventRegistrar *eventRegistrar;
//- (void)emitEvent:(ElectrodeBridgeEvent *)event id:(NSString *)id;
@end
