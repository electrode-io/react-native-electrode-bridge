//
//  ElectrodeRequestRegistrarNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/23/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeRequestRegistrarNew.h"
NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeRequestRegistrarNew ()

@property (nonatomic, strong) NSMutableDictionary *requestNameByUUID;
@property (nonatomic, strong) NSMutableDictionary *requestHandlerByRequestName;

@end

@implementation ElectrodeRequestRegistrarNew
- (NSUUID *)registerRequestHandler:(NSString *)name
                      requestHandler:(id<ElectrodeBridgeRequestHandler>)handler
                               error:(NSError **)error
{
    @synchronized (self) {
        if ([self.requestHandlerByRequestName objectForKey:name])
        {
            *error = [NSError errorWithDomain:@"Name Already In Use" code:37 userInfo:nil];
            return nil;
        }
        
        NSUUID *requestHandlerUUID = [NSUUID UUID];
        [self.requestHandlerByRequestName setObject:handler forKey:name];
        [self.requestNameByUUID setObject:name forKey:requestHandlerUUID];
        
        return requestHandlerUUID;
    }
}

- (void)unregisterRequestHandler:(NSUUID *)uuid
{
    @synchronized (self) {
        NSUUID *requestName = [self.requestNameByUUID objectForKey:uuid];

        if (requestName)
        {
            [self.requestNameByUUID removeObjectForKey:uuid];
            [self.requestHandlerByRequestName removeObjectForKey:requestName];
        }
    }
}

- (id<ElectrodeBridgeRequestHandler> _Nullable)getRequestHandler:(NSString *)name
{
    @synchronized (self) {
        return [self.requestHandlerByRequestName objectForKey:name];
    }
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
NS_ASSUME_NONNULL_END
