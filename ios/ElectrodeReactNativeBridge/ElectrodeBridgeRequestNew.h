//
//  ElectrodeBridgeRequestNew.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeMessage.h"
#import <Foundation/Foundation.h>
@class ElectrodeBridgeMessage;
NS_ASSUME_NONNULL_BEGIN
extern const int kElectrodeBridgeRequestDefaultTimeOut;
extern const int kElectrodeBridgeRequestNoTimeOut;


@interface ElectrodeBridgeRequestNew : ElectrodeBridgeMessage<NSCopying>
/**
 * return timeout of the request in ms.
 */
@property(nonatomic, assign, readonly) int timeoutMs;

/**
 * return if a request was initiated by JS.
 */
@property(nonatomic, assign, readonly) BOOL isJsInitiated;

+(nullable instancetype)createRequestWithData: (NSDictionary *)data;

- (instancetype)initWithName:(NSString *)name data:(nullable id)data;

@end
NS_ASSUME_NONNULL_END
