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


/**
 ElectrodeBridgeHolder helps native code talk to the bridge. All native code that 
 */
@interface ElectrodeBridgeHolder : NSObject
@property (nonatomic, weak) ElectrodeBridge *bridge;

/**
 Singleton -- duh
 */
+ (instancetype)sharedInstance;

/**
 Adds ability to be notified when the bridge is ready to go.
 */
- (void)registerStartupListener:(ElectrodeBridgeHolderListener)listenerBlock;

- (NSString *)registerEventListener:(NSString *)name eventListener:(id<ElectrodeEventListener>)eventListener;

- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)handler error:(NSError **)error;
@end
