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

/**
 ElectrodeRequestHandlers execute when a given request comes through. The 
 completioners execute once the request has fully been handled.
 */
@protocol ElectrodeRequestHandler <NSObject>

/**
 Initial request handling starts. Respond on success or error.

 @param data Data that is associated with a request, always in NSDictionary format.
 @param completioner The request completion that is executed when a request is 
 being processed.
 */
- (void)onRequest:(NSDictionary *)data
requestCompletioner:(id<ElectrodeRequestCompletioner>)completioner;

@end


////////////////////////////////////////////////////////////////////////////////
#pragma mark - ElectrodeRequestRegistrar

/**
 Register request handlers. These handlers will respond to specific requests 
 with a given name and ultimately fire off request completioners if they are 
 handled appropriately.
 */
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
