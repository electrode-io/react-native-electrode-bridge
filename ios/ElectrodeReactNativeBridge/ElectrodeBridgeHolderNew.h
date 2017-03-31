//
//  ElectrodeBridgeHolderNew.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/28/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
//#import "ElectrodeBridgeTransceiver.h"
#import "ElectrodeBridgeEventNew.h"
#import "ElectrodeBridgeRequestNew.h"
//#import "ElectrodeBridgeProtocols.h"

typedef void(^ElectrodeBridgeReactNativeReadyListner)();
@class ElectrodeBridgeTransceiver;
@protocol ElectrodeBridgeResponseListener, ElectrodeBridgeRequestHandler, ElectrodeBridgeEventListener;

NS_ASSUME_NONNULL_BEGIN

/**
 * Client facing class.
 * Facade to ElectrodeBridgeTransceiver.
 * Handles queuing every method calls until react native is ready.
 */

@interface ElectrodeBridgeHolderNew : NSObject
@property(nonatomic, assign, readonly) BOOL isReactNativeReady;

+ (instancetype)sharedInstance;

- (void)registerReactNativeReadyListener: (ElectrodeBridgeReactNativeReadyListner) listener;

- (void)sendEvent: (ElectrodeBridgeEventNew *)event;

- (void)sendRequest: (ElectrodeBridgeRequestNew *)request
   responseListener:(id<ElectrodeBridgeResponseListener> _Nonnull)responseListener;

- (void)registerRequestHanlderWithName: (NSString *)name
                        requestHandler: (id<ElectrodeBridgeRequestHandler> _Nonnull) requestHandler;

- (void)addEventListnerWithName: (NSString *)name
                   eventListner: (id<ElectrodeBridgeEventListener>) eventListner;

@end
NS_ASSUME_NONNULL_END


