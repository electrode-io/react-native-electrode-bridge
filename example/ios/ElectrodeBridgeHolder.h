//
//  ElectrodeBridgeHolder.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/27/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ElectrodeBridge, ElectrodeEventRegistrar;

typedef void (^ElectrodeBridgeHolderListener)(ElectrodeBridge *);

@interface ElectrodeBridgeHolder : NSObject
@property (nonatomic, weak) ElectrodeBridge *bridge;
@property (nonatomic, weak) ElectrodeEventRegistrar *eventRegistrar;

/**
 Singleton -- duh
 */
+ (instancetype)sharedInstance;

/**
 Adds ability to be notified when the bridge is ready to go.
 */
- (void)addListenerBlock:(ElectrodeBridgeHolderListener)listenerBlock;
@end
