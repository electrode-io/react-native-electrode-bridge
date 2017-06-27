//
//  ElectrodeBridgeHolder.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/28/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeEvent.h"
#import "ElectrodeBridgeRequest.h"

#import "ElectrodeBridgeProtocols.h"

@class ElectrodeBridgeTransceiver;
@protocol ElectrodeBridgeRequestHandler, ElectrodeBridgeEventListener;

NS_ASSUME_NONNULL_BEGIN

/**
 * Client facing class.
 * Facade to ElectrodeBridgeTransceiver.
 * Handles queuing every method calls until react native is ready.
 */

@interface ElectrodeBridgeHolder : NSObject

+ (void)sendEvent: (ElectrodeBridgeEvent *)event;

+ (void)sendRequest: (ElectrodeBridgeRequest *)request
  completionHandler: (ElectrodeBridgeResponseCompletionHandler) completion;

+ (void)registerRequestHanlderWithName: (NSString *)name
              requestCompletionHandler: (ElectrodeBridgeRequestCompletionHandler) completion;

+ (void)addEventListnerWithName: (NSString *)name
                   eventListner: (id<ElectrodeBridgeEventListener>) eventListner;

+ (void) setBridge: (ElectrodeBridgeTransceiver *)bridge;
+ (void)addConstantsProvider;
+ (void)addConstantsProvider:(id<ConstantsProvider>)constantsProvider;
@end
NS_ASSUME_NONNULL_END


