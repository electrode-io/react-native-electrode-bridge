//
//  ElectrodeBridgeProtocols.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeFailureMessage.h"
#import "ElectrodeBridgeRequest.h"
#import "ElectrodeBridgeEvent.h"

@class ElectrodeBridgeTransceiver;
NS_ASSUME_NONNULL_BEGIN
typedef void(^ElectrodeBridgeReactNativeReadyListner)(ElectrodeBridgeTransceiver *transceiver);



#pragma ElectrodeBridgeResponseListener protocol

/**
 
 */
typedef void(^ElectrodeBridgeResponseCompletionHandler) (id _Nullable data, id<ElectrodeFailureMessage> _Nullable message);
/**
 ElectrodeBridgeResponseCompletionHandler execute when a given request comes through. The
 completioners execute once the request has fully been handled.
 */
typedef void(^ElectrodeBridgeRequestCompletionHandler)(id _Nullable data, ElectrodeBridgeResponseCompletionHandler block);
/*
 * ElectrodeBridgeEventListener execute when an event is dispatched.
 */
typedef void(^ElectrodeBridgeEventListener) (id _Nullable eventPayload);

@interface ElectrodeBridgeProtocols : NSObject

@end


@protocol ConstantsProvider <NSObject>
/**
 * Returns constant values exposed to JavaScript.
 * Its implementation is not required but is very useful to key pre-defined values that need to be propagated from JavaScript to NativeiOS in sync
 * @return Dictionary containing a constant values
 */
- (NSDictionary<NSString *,id> *)constantsToExport;
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
 * @param completion The response call back listener to issue success/failure of the request.
 */
-(void)sendRequest:(ElectrodeBridgeRequest *)request
 completionHandler: (ElectrodeBridgeResponseCompletionHandler) completion;

/**
 * Register the request handler
 * @param name name of the request
 * @param completion call back to be issued for a given request.
 */
-(NSUUID *)registerRequestCompletionHandlerWithName: (NSString *)name
                         completionHandler: (ElectrodeBridgeRequestCompletionHandler) completion;

/**
 * Sends an event with payload to all the event listeners
 * @param event The event to emit
 */
-(void)sendEvent: (ElectrodeBridgeEvent *)event;

/**
 * Add an event listener for the passed event
 * @param name   The event name this listener is interested in 
 * @param eventListener The event listener
 * @return A UUID to pass back to unregisterEventListener
 */

- (NSUUID *)addEventListenerWithName: (NSString *)name eventListener: (ElectrodeBridgeEventListener) eventListener;

- (void)addConstantsProvider:(id<ConstantsProvider>)constantsProvider;

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

