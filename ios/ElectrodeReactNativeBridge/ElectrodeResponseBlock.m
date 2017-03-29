//
//  ElectrodeResponseBlock.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/16/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

#import "ElectrodeResponseBlock.h"

@implementation ElectrodeResponseBlock

- (instancetype)initWithSuccess:(ResponseSuccessBlock)successBlock failureBlock:(ResponseFailureBlock)failureBlock {
    
    self = [super init];
    if (self) {
        self.successBlock = successBlock;
        self.failureBlock = failureBlock;
    }
    return self;
}

- (void)onSuccess:(NSDictionary *)data {
    self.successBlock(data);
}

- (void)onError:(NSString *)code message:(NSString *)message {
    self.failureBlock(code, message);
}
@end
