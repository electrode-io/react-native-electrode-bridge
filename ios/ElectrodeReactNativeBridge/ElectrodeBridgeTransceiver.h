//
//  ElectrodeBridgeTransceiver.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>



#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#elif __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import "React/RCTBridgeModule.h"   // Required when used as a Pod in a Swift project
#endif

#if __has_include(<React/RCTEventEmitter.h>)
#import <React/RCTEventEmitter.h>
#elif __has_include("RCTEventEmitter.h")
#import "RCTEventEmitter.h"
#else
#import "React/RCTEventEmitter.h"   // Required when used as a Pod in a Swift project
#endif

#import "ElectrodeBridgeProtocols.h"
/**
 * A class that is responsible for transmitting messages between native side and react native side.
 */

@interface ElectrodeBridgeTransceiver : RCTEventEmitter<ElectrodeNativeBridge, ElectrodeReactBridge>

@property(nonatomic, copy, readonly) NSString *name;

+ (instancetype)sharedInstance;
+ (void)registerReactNativeReadyListener: (ElectrodeBridgeReactNativeReadyListner) reactNativeReadyListner;
- (void)onReactNativeInitialized;
+ (BOOL)isReactNativeBridgeReady;
@end
