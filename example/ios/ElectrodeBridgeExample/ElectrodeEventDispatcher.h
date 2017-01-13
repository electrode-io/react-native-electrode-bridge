//
//  ElectrodeEventDispatcher.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/19/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ElectrodeEventRegistrar;

////////////////////////////////////////////////////////////////////////////////
#pragma mark - Protocols
/**
 An event listener executes onEvent when the associated event listener is 
 executed for a given event.
 */
@protocol ElectrodeEventListener <NSObject>

/**
 The method that is executed when the event is received and processed.

 @param data Data that may or may not be present with a given event.
 */
- (void)onEvent:(NSDictionary *)data;

@end



////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeEventDispatcher

/**
 The Event Dispatcher (ElectrodeEventDispatcher) is responsible for firing 
 native events that have been registered for a name.
 */
@interface ElectrodeEventDispatcher : NSObject

/**
 The register mechanism to adding, removing and getting event listeners.
 */
@property (nonatomic, strong) ElectrodeEventRegistrar *eventRegistrar;


/**
 Execute an event listener for a given name on the native iOS side.

 @param event The name of the event to execute, reverse url format.
 @param id The UUID of the event.
 @param data The data that comes with a given event. Could be nil.
 */
- (void)dispatchEvent:(NSString *)event id:(NSString *)id data:(NSDictionary *)data;
@end
