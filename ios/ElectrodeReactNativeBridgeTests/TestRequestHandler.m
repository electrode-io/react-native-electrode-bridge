//
//  TestRequestHandler.m
//  ElectrodeReactNativeBridge
//
//  Created by Deepu Ganapathiyadan on 3/31/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "TestRequestHandler.h"

@implementation TestRequestHandler

- (instancetype)initWithOnRequestBlock:(onRequestBlock)onRequestBlk
{
    if(self = [super init])
    {
        self.onRequestBlk = onRequestBlk;
    }
    
    return self;
}

- (void)onRequest:(NSDictionary *)data responseListener:(id<ElectrodeBridgeResponseListener>)responseListener
{
    self.onRequestBlk(data, responseListener);
}

@end
