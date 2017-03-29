//
//  ElectrodeBridgeEventNew.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeMessage.h"
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeBridgeEventNew : ElectrodeBridgeMessage

+(nullable instancetype)createEventWithData: (NSDictionary *)data; //CLAIRE TODO: Why static methods vs just instance

@end

NS_ASSUME_NONNULL_END
