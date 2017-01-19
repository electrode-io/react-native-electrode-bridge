//
//  ElectrodeRequestDispatcher.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/5/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void (^ElectrodeRequestCompletionBlock)(NSDictionary * _Nullable data, NSError * _Nullable error);

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
- (void)onSuccess:(NSDictionary * _Nullable)data;


/**
 The method that is executed when an error happens for a request

 @param code The error code associated with the error
 @param message The message that is generated for the error
 */
- (void)onError:(NSString * _Nonnull)code message:(NSString * _Nonnull)message;

@end


////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeRequestCompletioner
/**
 * Provide methods to report request completion
 */
@protocol ElectrodeRequestCompletioner <NSObject>

- (instancetype _Nonnull)initWithCompletionBlock:(ElectrodeRequestCompletionBlock _Nonnull)completion;

/**
 Error response
 
 @param code The error code
 @param message The error message details
 */
- (void)error:(NSString * _Nonnull)code message:(NSString * _Nonnull)message;

/**
 Executes when a successul request happens and has data associated with it

 @param data NSDictionary of any key value pairs of data that may be expected.
 */
- (void)success:(NSDictionary * _Nonnull)data;

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

@property (nonatomic, strong, nonnull) ElectrodeRequestRegistrar *requestRegistrar;

- (void)dispatchRequest:(NSString * _Nonnull)name
                     id:(NSString * _Nonnull)requestID
                   data:(NSDictionary * _Nullable)data
             completion:(ElectrodeRequestCompletionBlock _Nonnull)completion;
@end

