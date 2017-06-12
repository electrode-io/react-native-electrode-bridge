//
//  ElectrodeEventRegistrar.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeProtocols.h"

@interface ElectrodeEventRegistrar : NSObject

/**
 Add a specific event listener that will respond to a given event name.
 
 @param name The name of the event in reverse url format.
 @param eventListener The event listener that will respond to a given event.
 @return The UUID of the registered event listener.
 */
- (NSUUID  * _Nonnull)registerEventListener:(NSString * _Nonnull)name
                                eventListener:(id<ElectrodeBridgeEventListener> _Nonnull)eventListener;

/**
 Remove an event listener by a given UUID. It is possible to have multiple event
 listeners for a given name. They are grouped by the name and separated by UUID.
 
 @param eventListenerUUID The UUID of the event listener.
 */
- (void)unregisterEventListener:(NSUUID  * _Nonnull)eventListenerUUID;

/**
 Grabs all of the event listeners of a given name.
 
 @param name The name that the event listeners will respond to.
 @return An array of all of the event listeners, will return nil if none are found.
 */
- (NSArray <ElectrodeBridgeEventListener> * _Nullable)getEventListnersForName:(NSString * _Nonnull)name;

@end
