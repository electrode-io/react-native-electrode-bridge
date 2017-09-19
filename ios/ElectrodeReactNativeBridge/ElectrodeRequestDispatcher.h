//
//  ElectrodeRequestDispatcher.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeRequestRegistrar.h"
#import "ElectrodeBridgeRequest.h"
#import "ElectrodeBridgeProtocols.h"

NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeRequestDispatcher : NSObject

@property(nonatomic, strong, readonly) ElectrodeRequestRegistrar *requestRegistrar;

- (instancetype)initWithRequestRegistrar:(ElectrodeRequestRegistrar *)requestRegistrar;
- (void)dispatchRequest:(ElectrodeBridgeRequest *)bridgeRequest
      completionHandler:(ElectrodeBridgeResponseCompletionHandler)completion;
- (BOOL)canHandlerRequestWithName:(NSString *)name;
@end

NS_ASSUME_NONNULL_END
