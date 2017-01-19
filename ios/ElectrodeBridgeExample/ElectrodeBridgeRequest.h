//
//  ElectrodeBridgeRequest.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/4/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ElectrodeBridge.h"

////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeBridgeRequest
/**
 The model for bridge requests to capture the name, data, mode and timeout.
 */
@interface ElectrodeBridgeRequest : NSObject

/**
 The name of the request in reverse url form.
 */
@property (nonatomic, readonly, nonnull) NSString *name;

/**
 The data that comes in the original request. Can be used to handle the request 
 or give information on paths.
 */
@property (nonatomic, readonly, nullable) NSDictionary *data;

/**
 The mode or endpoint of where the request should end up. Either in React Native
 or native iOS.
 */
@property (nonatomic, readonly) EBDispatchMode dispatchMode;

/**
 The time until the request becomes invalid.
 */
@property (nonatomic, readonly) NSInteger timeout;



/**
 Create a request with a default timeout of 5 seconds.

 @param name The name of the request in reverse url format.
 @param data The data following NSDictionary key values.
 @param mode The destination of the request, JS or Native.
 @return An instance of the request.
 */
- (instancetype _Nonnull)initWithName:(NSString * _Nonnull)name data:(NSDictionary * _Nullable)data mode:(EBDispatchMode)mode;

/**
 Create a request with a default timeout of 5 seconds.
 
 @param name The name of the request in reverse url format.
 @param data The data following NSDictionary key values.
 @param mode The destination of the request, JS or Native.
 @param timeout The time until the request timesout in seconds.
 @return An instance of the request.
 */
- (instancetype _Nonnull)initWithName:(NSString * _Nonnull)name data:(NSDictionary * _Nullable)data mode:(EBDispatchMode)mode timeout:(NSInteger)timeout;
@end
