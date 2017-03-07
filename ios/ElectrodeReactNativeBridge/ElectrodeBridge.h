//
//  ElectrodeBridge.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/12/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTBridgeModule.h"
#import "ElectrodeBridgeHolder.h"

/**
 Common Bridge codes used by external libraries
 */
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

@class ElectrodeBridgeEvent, ElectrodeEventRegistrar, ElectrodeRequestRegistrar, ElectrodeBridgeRequest;
@protocol ElectrodeRequestCompletionListener;

////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeBridge
/**
 ElectrodeBridge is responsible for dispatching and handling both events and 
 requests between React Native and iOS platforms. It allows native to native, 
 React Native to React Native, React Native to native and native to React Native 
 messaging to create a simplified way of communication between the two and 
 native components.
 
 Events are simple fire and forget, while requests require a response of sort. 
 Requests also have a timeout.
 */
@interface ElectrodeBridge : NSObject <RCTBridgeModule, ElectrodeBridgeInterface>

@end
