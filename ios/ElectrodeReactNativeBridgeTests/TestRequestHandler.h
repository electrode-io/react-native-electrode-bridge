//
//  TestRequestHandler.h
//  ElectrodeReactNativeBridge
//
//  Created by Deepu Ganapathiyadan on 3/31/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeProtocols.h"


typedef void(^onRequestBlock)(NSDictionary* data, id<ElectrodeBridgeResponseListener> responseListener);

@interface TestRequestHandler : NSObject<ElectrodeBridgeRequestHandler>

-(instancetype) initWithOnRequestBlock:(nonnull onRequestBlock) onRequestBlk;
@property(nonnull, nonatomic, copy) onRequestBlock onRequestBlk;

@end
