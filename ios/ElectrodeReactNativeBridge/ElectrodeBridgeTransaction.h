//
//  ElectrodeBridgeTransaction.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeRequestNew.h"
#import "ElectrodeBridgeResponse.h"

@interface ElectrodeBridgeTransaction : NSObject

@property(nonatomic, readonly, strong, nonnull) ElectrodeBridgeRequestNew *request;
@property(nonatomic, readonly, copy, nullable) ElectrodeBridgeResponseCompletionBlock completion;


// Note: response can be set
@property(nonatomic, readwrite, strong,nullable) ElectrodeBridgeResponse *response;

-(nonnull instancetype)initWithRequest: (ElectrodeBridgeRequestNew * _Nonnull) request
                     completionHandler: (ElectrodeBridgeResponseCompletionBlock _Nullable ) completion;
-(nonnull NSString *) transactionId;
-(BOOL) isJsInitiated;

@end
