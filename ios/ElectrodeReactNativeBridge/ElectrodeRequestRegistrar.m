//
//  ElectrodeRequestRegistrar.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/4/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "ElectrodeRequestRegistrar.h"

@interface ElectrodeRequestRegistrar ()

@property (nonatomic, strong) NSMutableDictionary *requestNameByUUID;
@property (nonatomic, strong) NSMutableDictionary *requestHandlerByRequestName;

@end

@implementation ElectrodeRequestRegistrar

- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)handler error:(NSError **)error
{
  if (!name || !handler)
  {
    *error = [NSError errorWithDomain:@"Pass an instance of a handler and name" code:38 userInfo:nil];
    return nil;
  }

  if ([self.requestHandlerByRequestName objectForKey:name])
  {
    *error = [NSError errorWithDomain:@"Name Already In Use" code:37 userInfo:nil];
    return nil;
  }

  NSString *requestHandlerUUID = [[NSUUID UUID] UUIDString];
  [self.requestHandlerByRequestName setObject:handler forKey:name];
  [self.requestNameByUUID setObject:name forKey:requestHandlerUUID];
  
  return requestHandlerUUID;
}

- (void)unregisterRequestHandler:(NSString *)uuid
{
  if (!uuid)
  {
    return;
  }
  
  NSString *requestName = [self.requestNameByUUID objectForKey:uuid];
  if (requestName)
  {
    [self.requestNameByUUID removeObjectForKey:uuid];
    [self.requestHandlerByRequestName removeObjectForKey:requestName];
  }
}

- (id<ElectrodeRequestHandler>)getRequestHandler:(NSString *)name
{
  return [self.requestHandlerByRequestName objectForKey:name];
}

////////////////////////////////////////////////////////////////////////////////
#pragma mark - Lazy Loading
- (NSMutableDictionary *)requestNameByUUID
{
  // Lazy instatiation
  if (!_requestNameByUUID)
  {
    _requestNameByUUID = [[NSMutableDictionary alloc] init];
  }
  
  return _requestNameByUUID;
}

- (NSMutableDictionary *)requestHandlerByRequestName
{
  // Lazy instatiation
  if (!_requestHandlerByRequestName)
  {
    _requestHandlerByRequestName = [[NSMutableDictionary alloc] init];
  }
  
  return _requestHandlerByRequestName;
}

@end
