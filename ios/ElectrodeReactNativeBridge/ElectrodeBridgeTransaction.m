//
//  ElectrodeBridgeTransaction.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransaction.h"
#import "ElectrodeBridgeMessage.h"

@interface ElectrodeBridgeTransaction()

@property(nonatomic, strong, nonnull) ElectrodeBridgeRequestNew *request;
@property(nonatomic, copy, nullable) ElectrodeBridgeResponseListenerSuccessBlock success;
@property(nonatomic, copy, nullable) ElectrodeBridgeResponseListenerFailureBlock failure;

@end

@implementation ElectrodeBridgeTransaction

-(nonnull instancetype)initWithRequest: (ElectrodeBridgeRequestNew * _Nonnull) request
                               success: (ElectrodeBridgeResponseListenerSuccessBlock _Nullable) success
                               failure: (ElectrodeBridgeResponseListenerFailureBlock _Nullable) failure
{
    if (request.type != ElectrodeMessageTypeRequest) {
        [NSException raise:@"Invalid type" format:@"BridgeTransaction constrictor expects a request type, did you accidentally pass in a different type"];
    }
    
    if (self = [super init]) {
        _request = request;
        _success = success;
        _failure = failure;
    }
    
    return self;
}

-(nonnull NSString *)transactionId {
    return self.request.messageId;
}
-(BOOL)isJsInitiated {
    return self.request.isJsInitiated;
}

@end
