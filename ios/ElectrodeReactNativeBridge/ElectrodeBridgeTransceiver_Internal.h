//
//  ElectrodeBridgeTransceiver_Internal.h
//  ElectrodeReactNativeBridge
//
//  Created by Deepu Ganapathiyadan on 3/31/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransceiver.h"
NS_ASSUME_NONNULL_BEGIN
static ElectrodeBridgeReactNativeReadyListner reactNativeReadyListener = nil;
static ElectrodeBridgeReactNativeReadyListner reactNativeTransceiver = nil;
static BOOL isReactNativeReady = NO;
static BOOL isTransceiverReady = NO;
static ElectrodeBridgeTransceiver *sharedInstance;

@interface ElectrodeBridgeTransceiver ()
-(void) emitMessage:(ElectrodeBridgeMessage * _Nonnull) bridgeMessage;
-(void)resetRegistrar;
@end

NS_ASSUME_NONNULL_END
