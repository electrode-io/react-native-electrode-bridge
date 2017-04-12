//
//  ElectrodeBridgeTransceiver_Internal.h
//  ElectrodeReactNativeBridge
//
//  Created by Deepu Ganapathiyadan on 3/31/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransceiver.h"
NS_ASSUME_NONNULL_BEGIN

@interface ElectrodeBridgeTransceiver ()
+ (instancetype) createWithTransceiver: (ElectrodeBridgeTransceiver *) transceiver;
-(void) emitMessage:(ElectrodeBridgeMessage * _Nonnull) bridgeMessage;
-(void)resetRegistrar;
@end

NS_ASSUME_NONNULL_END
