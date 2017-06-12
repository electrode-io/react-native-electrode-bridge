//
//  ElectrodeRequestDispatcher.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeRequestDispatcher.h"
#import "ElectrodeBridgeFailureMessage.h"

NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeRequestRegistrar()
@property(nonatomic, strong) ElectrodeRequestRegistrar *requestRegistrar;

@end

@implementation ElectrodeRequestDispatcher
-(instancetype)initWithRequestRegistrar: (ElectrodeRequestRegistrar *)requestRegistrar
{
    if (self = [super init]) {
        _requestRegistrar = [[ElectrodeRequestRegistrar alloc] init];
    }
    return self;
}

-(void)dispatchRequest: (ElectrodeBridgeRequest *)bridgeRequest
     completionHandler: (ElectrodeBridgeResponseCompletionHandler) completion

{
    NSString *requestId = bridgeRequest.messageId;
    NSString *requestName = bridgeRequest.name;
    
    NSLog(@"ElectrodeRequestDispatcher dispatching request(id=%@) locally", requestId);
    
    ElectrodeBridgeRequestCompletionHandler requestCompletionHandler = [self.requestRegistrar getRequestHandler:requestName];
    if (requestCompletionHandler == nil)
    {
        NSString *errorMessage = [NSString stringWithFormat:@"No registered request handler for request name %@", requestName];
        id<ElectrodeFailureMessage> failureMessage = [ElectrodeBridgeFailureMessage createFailureMessageWithCode:@"ENOHANDLER" message:errorMessage];
        if (completion) {
            completion(nil, failureMessage);
        }
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        requestCompletionHandler(bridgeRequest.data, completion);
    });
}
-(BOOL)canHandlerRequestWithName: (NSString *)name
{
    return ([self.requestRegistrar getRequestHandler:name] != nil);
}
@end

NS_ASSUME_NONNULL_END
