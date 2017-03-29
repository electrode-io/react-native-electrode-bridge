//
//  ElectrodeBridgeResponse.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeFailureMessage.h"
#import "ElectrodeBridgeRequestNew.h"

NS_ASSUME_NONNULL_BEGIN



/**
 *
 */

extern NSString * const kElectrodeBridgeResponseError;
extern NSString * const kElectrodeBridgeResponseErrorCode;
extern NSString * const kElectrodeBridgeResponseErrorMessage;
extern NSString * const kElectrodeBridgeResponseUnknownErrorCode;

@interface ElectrodeBridgeResponse : ElectrodeBridgeMessage

@property(readonly, nonatomic, strong, nullable) id<ElectrodeFailureMessage> failureMessage;

+(nullable instancetype)createResponseWithData: (NSDictionary *)data;
+(nullable instancetype)createResponseForRequest: (ElectrodeBridgeRequestNew *)request
                                        withResponseData: (nullable NSDictionary *)data
                              withFailureMessage: (nullable id<ElectrodeFailureMessage>)failureMessage;
@end

NS_ASSUME_NONNULL_END
