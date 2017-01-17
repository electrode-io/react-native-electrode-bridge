//
//  ElectrodeRequestDispatcher.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/5/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^ElectrodeRequestCompletionBlock)(NSDictionary *data, NSError *error);

@class ElectrodeRequestRegistrar;

////////////////////////////////////////////////////////////////////////////////
#pragma mark - Protocols
/**
 Provide methods to complete requests
 */
@protocol ElectrodeRequestCompletionListener <NSObject>

/**
 The method that is executed when a request listener is executed for a given 
 request.
 
 @param data The data that may be attached to a request being returned.
 */
- (void)onSuccess:(NSDictionary *)data;


/**
 The method that is executed when an error happens for a request

 @param code The error code associated with the error
 @param message The message that is generated for the error
 */
- (void)onError:(NSString *)code message:(NSString *)message;

@end


////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeRequestCompletioner
/**
 * Provide methods to report request completion
 */
@protocol ElectrodeRequestCompletioner <NSObject>

- (instancetype)initWithCompletionBlock:(ElectrodeRequestCompletionBlock)completion;

/**
 Error response
 
 @param code The error code
 @param message The error message details
 */
- (void)error:(NSString *)code message:(NSString *)message;

/**
 Executes when a successul request happens and has data associated with it

 @param data NSDictionary of any key value pairs of data that may be expected.
 */
- (void)success:(NSDictionary *)data;

/**
 Executes when a successul request happens and does not have data.
 */
- (void)success;

@end


////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeRequestDispatcher

/**
 Provides the ability to dispatch a request
 */
@interface ElectrodeRequestDispatcher : NSObject

@property (nonatomic, strong) ElectrodeRequestRegistrar *requestRegistrar;

- (void)dispatchRequest:(NSString *)name id:(NSString *)requestID data:(NSDictionary *)data completion:(ElectrodeRequestCompletionBlock)completion;
@end

