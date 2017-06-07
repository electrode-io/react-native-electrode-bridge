//
//  ElectrodeRequestDispatcherNew.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeRequestRegistrarNew.h"
#import "ElectrodeBridgeRequestnew.h"
#import "ElectrodeBridgeProtocols.h"

NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeRequestDispatcherNew : NSObject

@property(nonatomic, strong, readonly) ElectrodeRequestRegistrarNew *requestRegistrar;

-(instancetype)initWithRequestRegistrar: (ElectrodeRequestRegistrarNew *)requestRegistrar;
-(void)dispatchRequest: (ElectrodeBridgeRequestNew *)bridgeRequest
     completionHandler: (ElectrodeBridgeResponseCompletionBlock) completion;
-(BOOL)canHandlerRequestWithName: (NSString *)name;
@end
                       
NS_ASSUME_NONNULL_END
