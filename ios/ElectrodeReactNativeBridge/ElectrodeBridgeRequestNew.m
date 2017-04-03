//
//  ElectrodeBridgeRequestNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeRequestNew.h"

const int kElectrodeBridgeRequestDefaultTimeOut = 5000;


@interface ElectrodeBridgeRequestNew()

@property(nonatomic, assign) int timeoutMs;
@property(nonatomic, assign) BOOL isJsInitiated;

@end

@implementation ElectrodeBridgeRequestNew

+(nullable instancetype)createRequestWithData: (NSDictionary *)data {
    if ([super isValidFromData:data withType:ElectrodeMessageTypeRequest]) {
        return [[self alloc] initWithData:data];
    }
    
    NSLog(@"cannot create class ElectrodeBridgeRequestNew with data");
    return nil;
}

- (instancetype)initWithData:(NSDictionary *)data
{
    if(self = [super initWithData:data])
    {
        _timeoutMs = kElectrodeBridgeRequestDefaultTimeOut;
        _isJsInitiated = YES;
    }
    return self;
}

@end
