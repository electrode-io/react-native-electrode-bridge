//
//  ElectrodeEventDispatcher.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeEventRegistrar.h"
#import "ElectrodeBridgeEvent.h"

NS_ASSUME_NONNULL_BEGIN

@interface ElectrodeEventDispatcher : NSObject

@property(nonatomic, strong, readonly) ElectrodeEventRegistrar *eventRegistrar;

-(instancetype)initWithEventRegistrar: (ElectrodeEventRegistrar *)eventRegistrar;
-(void)dispatchEvent: (ElectrodeBridgeEvent *)bridgeEvent;

@end

NS_ASSUME_NONNULL_END
