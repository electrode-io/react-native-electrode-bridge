//
//  ElectrodeEventDispatcherNew.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeEventRegistrarNew.h"
#import "ElectrodeBridgeEventNew.h"

NS_ASSUME_NONNULL_BEGIN

@interface ElectrodeEventDispatcherNew : NSObject

@property(nonatomic, strong, readonly) ElectrodeEventRegistrarNew *eventRegistrar;

-(instancetype)initWithEventRegistrar: (ElectrodeEventRegistrarNew *)eventRegistrar;
-(void)dispatchEvent: (ElectrodeBridgeEventNew *)bridgeEvent;

@end

NS_ASSUME_NONNULL_END
