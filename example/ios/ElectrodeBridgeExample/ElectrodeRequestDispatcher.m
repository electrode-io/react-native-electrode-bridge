//
//  ElectrodeRequestDispatcher.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/5/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "ElectrodeRequestDispatcher.h"
#import "ElectrodeRequestRegistrar.h"


@interface ElectrodeRequestCompletionerImplentator : NSObject <ElectrodeRequestCompletioner>
@property (nonatomic, copy) ElectrodeRequestCompletionBlock completionBlock;
@end

@implementation ElectrodeRequestCompletionerImplentator

- (instancetype)initWithCompletionBlock:(ElectrodeRequestCompletionBlock)completion
{
  self = [super init];
  if (self)
  {
    self.completionBlock = completion;
  }
  return self;
}

- (void)error:(NSString *)code message:(NSString *)message
{
  self.completionBlock(nil, [NSError errorWithDomain:message code:code.integerValue userInfo:nil]);
}

- (void)success:(NSDictionary *)data
{
  self.completionBlock(data, nil);
}

- (void)success
{
  self.completionBlock(@{}, nil);
}

@end



@implementation ElectrodeRequestDispatcher

- (void)dispatchRequest:(NSString *)name id:(NSString *)requestID data:(NSDictionary *)data completion:(ElectrodeRequestCompletionBlock)completion
{
  id<ElectrodeRequestHandler> requestHandler = [self.requestRegistrar getRequestHandler:name];
  
  if (!requestHandler)
  {
    completion(nil, [NSError errorWithDomain:@"No Handler" code:-122 userInfo:nil]);
    return;
  }
  
  dispatch_async(dispatch_get_main_queue(), ^{
    
    // TODO: Add timeout here
    [requestHandler onRequest:data requestCompletioner:[[ElectrodeRequestCompletionerImplentator alloc] initWithCompletionBlock:completion]];
  });
  
}

- (ElectrodeRequestRegistrar *)requestRegistrar
{
  // Lazy initialization
  if (!_requestRegistrar)
  {
    _requestRegistrar = [[ElectrodeRequestRegistrar alloc] init];
  }
  
  return _requestRegistrar;
}

@end
