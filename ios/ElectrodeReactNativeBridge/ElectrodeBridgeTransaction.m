 
                            
                          
                        
        
    
  
//
//  ElectrodeBridgeTransaction.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransaction.h"
#import "ElectrodeBridgeMessage.h"
NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeBridgeTransaction ()

@property(nonatomic, strong, nonnull) ElectrodeBridgeRequest *request;
@property(nonatomic, strong, nullable)
    ElectrodeBridgeResponseCompletionHandler completion;

@end

@implementation ElectrodeBridgeTransaction

- (nonnull instancetype)initWithRequest:(ElectrodeBridgeRequest *)request
                      completionHandler:
                          (ElectrodeBridgeResponseCompletionHandler _Nullable)
                              completion;
{
  if (request.type != ElectrodeMessageTypeRequest) {
    [NSException raise:@"Invalid type"
                format:@"BridgeTransaction constrictor expects a request type, "
                       @"did you accidentally pass in a different type"];
  }

  if (self = [super init]) {
    _request = request;
    _completion = completion;
  }

  return self;
}

- (nonnull NSString *)transactionId {
  return self.request.messageId;
}
- (BOOL)isJsInitiated {
  return self.request.isJsInitiated;
}

@end
NS_ASSUME_NONNULL_END
