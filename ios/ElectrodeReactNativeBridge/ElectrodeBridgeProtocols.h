//
//  ElectrodeBridgeProtocols.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeFailureMessage.h"
#import "ElectrodeBridgeRequestNew.h"
#import "ElectrodeBridgeEventNew.h"


NS_ASSUME_NONNULL_BEGIN
typedef void(^ElectrodeBridgeReactNativeReadyListner)();



#pragma ElectrodeBridgeResponseListener protocol

typedef void(^ElectrodeBridgeResponseListenerSuccessBlock) (id nullable);
typedef void(^ElectrodeBridgeResponseListenerFailureBlock)(id<ElectrodeFailureMessage>);

@protocol ElectrodeBridgeResponseListener <NSObject>

-(void)onFailure:(id<ElectrodeFailureMessage>)failureMessage;
-(void)onSuccess:(id _Nullable)responseData;

@end
////////////////////////////////////////////////
#pragma ElectrodeBridgeRequestHandler protocol
/**
 ElectrodeRequestHandlers execute when a given request comes through. The
 completioners execute once the request has fully been handled.
 */
@protocol ElectrodeBridgeRequestHandler <NSObject>

/**
 Initial request handling starts. Respond on success or error.
 
 @param data Data that is associated with a request, can be a NSDictionary or an Object
 @param responseListener The request completion that is executed when a request is
 being processed.
 */
- (void)onRequest:(id _Nullable)data responseListener:(id<ElectrodeBridgeResponseListener>)responseListener;

@end

////////////////////////////////////////////////
#pragma ElectrodeBridgeEventListener protocol
@protocol ElectrodeBridgeEventListener <NSObject>

/*
 * @param eventPayload that associated with the event. Can be an object or NSDictionary. 
 */
- (void)onEvent:(id _Nullable)eventPayload;

@end

@interface ElectrodeBridgeProtocols : NSObject

@end

////////////////////////////////////////////////
#pragma ElectrodeNativeBridge protocol
/*
 * Native client facing bridge API. Define all the actions a native client can perform over the bridge.
 */
@protocol ElectrodeNativeBridge <NSObject>

/**
 * Send a request from iOS native side to either native or React Native side depending on where the request handler is registered.
 * @param request    The ElectrodeBridgeRequest that contains request name, data, destination mode and timeout 
 * @param responseListener The response call back listener to issue success/failure of the request.
 */
-(void)sendRequest:(ElectrodeBridgeRequestNew *)request withResponseListener:(id<ElectrodeBridgeResponseListener>) responseListener;

/**
 * Register the request handler
 * @param name name of the request
 * @param requestHandler call back to be issued for a given request.
 */
-(NSUUID *)regiesterRequestHandlerWithName: (NSString *)name
                                   handler:(id<ElectrodeBridgeRequestHandler>)requestHandler
                                     error: (NSError **) error;

/**
 * Sends an event with payload to all the event listeners
 * @param event The event to emit
 */
-(void)sendEvent: (ElectrodeBridgeEventNew *)event;

/**
 * Add an event listener for the passed event
 * @param name   The event name this listener is interested in 
 * @param eventListener The event listener
 * @return A UUID to pass back to unregisterEventListener
 */

-(NSUUID *)addEventListenerWithName: (NSString *)name eventListener: (id<ElectrodeBridgeEventListener>) eventListener;

@end


////////////////////////////////////////////////
#pragma ElectrodeReactBridge protocol

/**
 * React facing bridge API. React Native side calls to talk to bridge
 */
@protocol ElectrodeReactBridge <NSObject>
/**
 * Invoked by React side to communicate the bridge
 * @params bridgeMessage  The NSDictionary representation of BridgeMessage
 */

- (void)sendMessage:(NSDictionary *)bridgeMessage;

@end

NS_ASSUME_NONNULL_END

