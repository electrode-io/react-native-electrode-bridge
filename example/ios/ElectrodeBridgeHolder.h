//
//  ElectrodeBridgeHolder.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/27/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ElectrodeBridge, ElectrodeEventRegistrar;
@protocol ElectrodeEventListener, ElectrodeRequestHandler;


/**
 A block type that is used to register a handler when the bridge is started up.

 @param bridge The main ElectrodeBridge is passed into the closure.
 */
typedef void (^ElectrodeBridgeHolderListener)(ElectrodeBridge *bridge);

////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeBridgeHolder
/**
 ElectrodeBridgeHolder helps native code talk to the bridge. All native code that
 */
@interface ElectrodeBridgeHolder : NSObject
@property (nonatomic, weak) ElectrodeBridge *bridge;

/**
 The bridge holder is a singleton.
 */
+ (instancetype)sharedInstance;

/**
 Adds ability to be notified when the bridge is ready to go.

 @param listenerBlock The block that is executed when the bridge becomes active.
 It is also fired immediately if the bridge is already active.
 */
- (void)registerStartupListener:(ElectrodeBridgeHolderListener)listenerBlock;

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
 @param handler The request handler that handles executing when the request goes 
 through the bridge.
 @param error An NSError is used in case a handler is already registered for 
 that name.
 @return The UUID of the request handler.
 */
- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)handler error:(NSError **)error;
@end
