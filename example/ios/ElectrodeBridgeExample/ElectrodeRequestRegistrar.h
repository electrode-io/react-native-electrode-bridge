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

////////////////////////////////////////////////////////////////////////////////
#pragma mark - Protocols
@protocol ElectrodeRequestHandler <NSObject>

- (void)onRequest:(NSDictionary *)data
requestCompletioner:(id<ElectrodeRequestCompletioner>)completioner;

@end


////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeRequestRegistrar
@interface ElectrodeRequestRegistrar : NSObject


/**
 Register a request handler with a given name. An error is returned if a handler 
 already exists for the given name. Only one request is allowed per name.

 @param name The name of the event in reverse url format.
 @param handler The request handler that will parse and process a request.
 @param error The error is returned if a request already exists for that name.
 @return A UUID is returned for a request being added.
 */
- (NSString *)registerRequestHandler:(NSString *)name
                      requestHandler:(id<ElectrodeRequestHandler>)handler
                               error:(NSError **)error;

/**
 * Unregisters a request handler
 *
 * @param uuid - The UUID that was obtained through initial registerRequestHandler
 * call
 */
- (void)unregisterRequestHandler:(NSString *)uuid;


/**
 Grabs a given request handler for a request name.

 @param name The name of the request, in reverse url format.
 @return Returns a request handler for a specific name.
 */
- (id<ElectrodeRequestHandler>)getRequestHandler:(NSString *)name;

@end
