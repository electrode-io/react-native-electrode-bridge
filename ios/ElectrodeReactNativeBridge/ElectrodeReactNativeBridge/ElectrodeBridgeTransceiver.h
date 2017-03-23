//
//  ElectrodeBridgeTransceiver.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

#import "ElectrodeBridgeProtocols.h"
/**
 * A class that is responsible for transmitting messages between native side and react native side.
 */

@interface ElectrodeBridgeTransceiver : NSObject<RCTBridgeModule, ElectrodeNativeBridge, ElectrodeReactBridge>

@property(nonatomic, copy, readonly) NSString *name;

+ (instancetype)sharedInstance;

@end
