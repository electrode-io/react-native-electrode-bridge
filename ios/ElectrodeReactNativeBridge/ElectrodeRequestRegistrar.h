//
//  ElectrodeRequestRegistrar.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/23/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeBridgeProtocols.h"

NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeRequestRegistrar : NSObject

/**
 Register a request handler with a given name. An error is returned if a handler
 already exists for the given name. Only one request is allowed per name.

 @param name The name of the event in reverse url format.
 @param completion The request handler that will parse and process a request.
 @return A UUID is returned for a request being added.
 */
- (NSUUID *)
registerRequestCompletionHandlerWithName:(NSString *)name
                              completion:
                                  (ElectrodeBridgeRequestCompletionHandler)
                                      completion;

/**
 * Unregisters a request handler
 *
 * @param uuid - The UUID that was obtained through initial
 * registerRequestHandler
 * call
 */
- (void)unregisterRequestHandler:(NSUUID *)uuid;

/**
 Grabs a given request handler for a request name.

 @param name The name of the request, in reverse url format.
 @return Returns a request handler for a specific name.
 */
- (nullable ElectrodeBridgeRequestCompletionHandler)getRequestHandler:
    (NSString *)name;
- (void)reset;

@end

NS_ASSUME_NONNULL_END
