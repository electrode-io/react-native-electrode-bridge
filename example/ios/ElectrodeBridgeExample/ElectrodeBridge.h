//
//  ElectrodeBridge.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/12/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTBridgeModule.h"

/**
 Commong Bridge codes used by external libraries
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


/**
 The mode determining the path of the request or event. If it is JS, the event 
 or request is handled by javascript (or React Native). If it is Native the event
 is handled by iOS. If it is Global, both javascript and iOS handle the event.

 - JS: Javascript / React Native handles the event or request.
 - NATIVE: iOS handles the event or request.
 - GLOBAL: Both javascript and iOS handle the event. Works for events only.
 */
typedef NS_ENUM(NSInteger, EBDispatchMode)
{
  JS,
  NATIVE,
  GLOBAL
};

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
@interface ElectrodeBridge : NSObject <RCTBridgeModule>


/**
 Use the eventRegistrar to register event listeners that can handle events coming
 through the bridge.
 */
@property (nonatomic, readonly) ElectrodeEventRegistrar *eventRegistrar;


/**
 Use the requestRegistrar to register request handlers that can respond with 
 functionality to requests.
 */
@property (nonatomic, readonly) ElectrodeRequestRegistrar *requestRegistrar;


/**
 Send an event from native.

 @param event The event object that will be dispersed throughout the system.
 */
- (void)emitEvent:(ElectrodeBridgeEvent *)event;

/**
 Send a request from iOS to either iOS or React Native.

 @param request The ElectrodeBridgeRequest that will contain the request name, 
 data, destination mode, and timeout.
 @param listener The completion handler that is executed when the request 
 succeeds or fails.
 */
- (void)sendRequest:(ElectrodeBridgeRequest *)request completionListener:(id<ElectrodeRequestCompletionListener>)listener;
@end
