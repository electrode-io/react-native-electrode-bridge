//
//  ElectrodeBridgeTransaction.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeRequest.h"
#import "ElectrodeBridgeResponse.h"
NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeBridgeTransaction : NSObject

@property(nonatomic, readonly, strong) ElectrodeBridgeRequest *request;
@property(nonatomic, readonly, strong, nullable) ElectrodeBridgeResponseCompletionHandler completion;
// Note: response can be set
@property(nonatomic, readwrite, strong, nullable)
    ElectrodeBridgeResponse *response;

- (instancetype)initWithRequest:(ElectrodeBridgeRequest *)request
              completionHandler:(ElectrodeBridgeResponseCompletionHandler _Nullable)completion;
- (NSString *)transactionId;
- (BOOL)isJsInitiated;

@end
NS_ASSUME_NONNULL_END
