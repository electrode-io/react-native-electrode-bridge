//
//  ElectrodeBridgeEvent.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/16/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ElectrodeBridgeHolder.h"

////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeBridgeEvent
/**
 The ElectrodeBridgeEvent model for events that will contain the event dispatch 
 name, data that will be received and the destination mode. Used to bottle up 
 event data that will be sent to either React Native or iOS.
 */
@interface ElectrodeBridgeEvent : NSObject


/**
 The reverse name to differentiate events that are coming in.
 */
@property (nonatomic, readonly, nonnull) NSString *name;


/**
 The data that is associated with the event to be received. Data can be nil, and 
 is always in the form of a dictionary.
 */
@property (nonatomic, readonly, nullable) NSDictionary *data;


/**
 The destination mode of where the event is intended to be sent e.g. 
 JS, NATIVE, GLOBAL
 */
@property (nonatomic, readonly) EBDispatchMode dispatchMode;


/**
 Convenience initializer to create the bridge event with all of the appropriate 
 information.

 @param name The name of the event in reverse lookup, e.g. com.electrode.event.
 @param data Any data that may need to be transported to the received, can be nil.
 @param mode The intended destination.
 @return ElectrodeBridgeEvent is returned.
 */
- (instancetype _Nonnull)initWithName:(NSString * _Nonnull)name data:(NSDictionary * _Nullable)data mode:(EBDispatchMode)mode;
@end
