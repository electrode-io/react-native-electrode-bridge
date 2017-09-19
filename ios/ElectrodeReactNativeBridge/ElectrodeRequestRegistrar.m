//
//  ElectrodeRequestRegistrar.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/23/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeRequestRegistrar.h"
#import "ElectrodeLogger.h"
NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeRequestRegistrar ()

@property(nonatomic, strong) NSMutableDictionary *requestNameByUUID;
@property(nonatomic, strong) NSMutableDictionary *requestHandlerByRequestName;

@end

@implementation ElectrodeRequestRegistrar

- (NSUUID *)
registerRequestCompletionHandlerWithName:(NSString *)name
                              completion:
                                  (ElectrodeBridgeRequestCompletionHandler)
                                      completion {
  @synchronized(self) {
    ERNDebug(@"***Logging registering requestHandler with Name %@", name);
    NSUUID *requestHandlerUUID = [NSUUID UUID];
    [self.requestHandlerByRequestName setObject:completion forKey:name];
    [self.requestNameByUUID setObject:name forKey:requestHandlerUUID];
    ERNDebug(@"***Logging registered requestHandlerDictionary:%@",
             self.requestHandlerByRequestName);
    return requestHandlerUUID;
  }
}

- (void)unregisterRequestHandler:(NSUUID *)uuid {
  @synchronized(self) {
    NSUUID *requestName = [self.requestNameByUUID objectForKey:uuid];

    if (requestName) {
      [self.requestNameByUUID removeObjectForKey:uuid];
      [self.requestHandlerByRequestName removeObjectForKey:requestName];
    }
  }
}

- (nullable ElectrodeBridgeRequestCompletionHandler)getRequestHandler:
    (NSString *)name;
{
  ERNDebug(@"***Logging getting request handler requestHandlerDictionary:%@",
           self.requestHandlerByRequestName);
  ERNDebug(@"%@", self);

  @synchronized(self) {
    return [self.requestHandlerByRequestName objectForKey:name];
  }
}

- (void)reset {
  self.requestNameByUUID = [[NSMutableDictionary alloc] init];
  self.requestHandlerByRequestName = [[NSMutableDictionary alloc] init];
}
////////////////////////////////////////////////////////////////////////////////
#pragma mark - Lazy Loading

- (NSMutableDictionary *)requestNameByUUID {
  // Lazy instatiation
  if (!_requestNameByUUID) {
    _requestNameByUUID = [[NSMutableDictionary alloc] init];
  }

  return _requestNameByUUID;
}

- (NSMutableDictionary *)requestHandlerByRequestName {
  // Lazy instatiation
  if (!_requestHandlerByRequestName) {
    _requestHandlerByRequestName = [[NSMutableDictionary alloc] init];
  }

  return _requestHandlerByRequestName;
}

@end
NS_ASSUME_NONNULL_END
