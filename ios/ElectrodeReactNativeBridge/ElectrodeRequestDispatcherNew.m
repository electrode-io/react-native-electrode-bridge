//
//  ElectrodeRequestDispatcherNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeRequestDispatcherNew.h"
#import "ElectrodeBridgeFailureMessage.h"


NS_ASSUME_NONNULL_BEGIN


@interface ElectrodeRequestDispatcherNew()
@property(nonatomic, strong) ElectrodeRequestRegistrarNew *requestRegistrar;

@end

@implementation ElectrodeRequestDispatcherNew
-(instancetype)initWithRequestRegistrar: (ElectrodeRequestRegistrarNew *)requestRegistrar
{
    if (self = [super init]) {
        _requestRegistrar = [[ElectrodeRequestRegistrarNew alloc] init];
    }
    return self;
}

-(void)dispatchRequest: (ElectrodeBridgeRequestNew *)bridgeRequest
  withResponseListener: (id<ElectrodeBridgeResponseListener>) responseListener
{
    NSString *requestId = bridgeRequest.messageId;
    NSString *requestName = bridgeRequest.name;
    
    NSLog(@"ElectrodeRequestDispatcherNew dispatching request(id=%@) locally", requestId);
    
    id<ElectrodeBridgeRequestHandler> requestHandler = [self.requestRegistrar getRequestHandler:requestName];
    if (requestHandler == nil || ![requestHandler conformsToProtocol:@protocol(ElectrodeBridgeRequestHandler)])
    {
        NSString *errorMessage = [NSString stringWithFormat:@"No registered request handler for request name %@", requestName];
        id<ElectrodeFailureMessage> failureMessage = [ElectrodeBridgeFailureMessage createFailureMessageWithCode:@"ENOHANDLER" message:errorMessage];
        [responseListener onFailure:failureMessage];
        return;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [requestHandler onRequest:bridgeRequest.data responseListener:responseListener];
    });
}
-(BOOL)canHandlerRequestWithName: (NSString *)name
{
    return ([self.requestRegistrar getRequestHandler:name] != nil);
}
@end

NS_ASSUME_NONNULL_END
