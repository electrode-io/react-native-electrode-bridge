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
@property(nonatomic, strong, nullable) id<ElectrodeBridgeResponseListener> finalResponseListener;

@end

@implementation ElectrodeBridgeTransaction

-(nonnull instancetype)initWithRequest: (nonnull ElectrodeBridgeRequestNew *)request
                      responseListener: (nullable id<ElectrodeBridgeResponseListener>) responseListener {
    if (request.type != ElectrodeMessageTypeRequest) {
        [NSException raise:@"Invalid type" format:@"BridgeTransaction constrictor expects a request type, did you accidentally pass in a different type"];
    }
    
    if (self = [super init]) {
        _request = request;
        _finalResponseListener  = responseListener;
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
