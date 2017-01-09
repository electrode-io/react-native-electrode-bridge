//
//  ElectrodeRequestRegistrar.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/4/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ElectrodeRequestCompletioner;
@protocol ElectrodeRequestCompletioner;


@protocol ElectrodeRequestHandler <NSObject>

- (void)onRequest:(NSDictionary *)data requestCompletioner:(id<ElectrodeRequestCompletioner>)completioner;

@end



@interface ElectrodeRequestRegistrar : NSObject

- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)handler error:(NSError **)error;

/**
 * Unregisters a request handler
 *
 * @param uuid - The UUID that was obtained through initial registerRequestHandler
 * call
 */
- (void)unregisterRequestHandler:(NSString *)uuid;

- (id<ElectrodeRequestHandler>)getRequestHandler:(NSString *)name;

@end
