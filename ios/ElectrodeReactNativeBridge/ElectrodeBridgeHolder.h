//
//  ElectrodeBridgeHolder.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/27/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ElectrodeEventRegistrar, ElectrodeRequestRegistrar, ElectrodeBridgeEvent, ElectrodeBridgeRequest;
@protocol ElectrodeEventListener, ElectrodeRequestHandler, ElectrodeRequestCompletionListener;

@protocol ElectrodeBridgeInterface <NSObject>

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
 @param completionListener The completion handler that is executed when the request
 succeeds or fails.
 */
- (void)sendRequest:(ElectrodeBridgeRequest *)request completionListener:(id<ElectrodeRequestCompletionListener>)completionListener;
@end


////////////////////////////////////////////////////////////////////////////////
#pragma mark - Enums
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
    EBDispatchModeJS,
    EBDispatchModeNative,
    EBDispatchModeGlobal
};


/**
 A block type that is used to register a handler when the bridge is started up.
 */
typedef void (^ElectrodeBridgeHolderListener)();

////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeBridgeHolder
/**
 ElectrodeBridgeHolder helps native code talk to the bridge. All native code that
 */
@interface ElectrodeBridgeHolder : NSObject
//@property (nonatomic, weak) ElectrodeBridge *bridge;

/**
 The bridge holder is a singleton.
 */
+ (instancetype)sharedInstance;

+ (NSArray *)electrodeModules;

/**
 Adds ability to be notified when the bridge is ready to go.

 @param listenerBlock The block that is executed when the bridge becomes active.
 It is also fired immediately if the bridge is already active.
 */
- (void)setOnBridgeReadyListener:(ElectrodeBridgeHolderListener)listenerBlock;

/**
 Add an event listener that handles executing when a specific named event is 
 fired.

 @param name The name of the event in reverse url fashion.
 @param eventListener The event listener that handles executing when the event 
 goes through the bridge.
 @return The UUID of the event listener.
 */
- (NSString *)registerEventListener:(NSString *)name eventListener:(id<ElectrodeEventListener>)eventListener;

/**
 Add a request handler that handles responding to requests by a certain name when
 that request comes through the bridge.

 @param name The name of the request in reverse url fashion.
 @param requestHandler The request handler that handles executing when the request goes 
 through the bridge.
 @param error An NSError is used in case a handler is already registered for 
 that name.
 @return The UUID of the request handler.
 */
- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)requestHandler error:(NSError **)error;

/**
 Send a request from iOS to either iOS or React Native.
 
 @param request The ElectrodeBridgeRequest that will contain the request name,
 data, destination mode, and timeout.
 @param completionListener The completion handler that is executed when the request
 succeeds or fails.
 */
- (void)sendRequest:(ElectrodeBridgeRequest *)request completionListener:(id<ElectrodeRequestCompletionListener>)completionListener;

/**
 Send an event from native.
 
 @param event The event object that will be dispersed throughout the system.
 */
- (void)emitEvent:(ElectrodeBridgeEvent *)event;

/**
 Associates a RCT Bridge module with the holder. This is a hard requirement to 
 set up the holder and alert others when it is ready.

 @param bridge An instance of ElectrodeBridge
 */
- (void)setBridge:(id<ElectrodeBridgeInterface>)bridge;

@end
