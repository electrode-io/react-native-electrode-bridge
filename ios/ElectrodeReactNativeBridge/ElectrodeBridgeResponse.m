//
//  ElectrodeBridgeResponse.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeResponse.h"
#import "ElectrodeBridgeFailureMessage.h"

NSString *const kElectrodeBridgeResponseError = @"error";
NSString *const kElectrodeBridgeResponseErrorCode = @"code";
NSString *const kElectrodeBridgeResponseErrorMessage = @"message";
NSString *const kElectrodeBridgeResponseUnknownErrorCode = @"EUNKNOWN";

@interface ElectrodeBridgeResponse ()

@property(nonatomic, strong, nullable) id<ElectrodeFailureMessage>
    failureMessage;

@end

@implementation ElectrodeBridgeResponse

+ (nullable instancetype)createResponseWithData:(NSDictionary *)data {
  if ([super isValidFromData:data withType:ElectrodeMessageTypeResponse]) {
    return [[ElectrodeBridgeResponse alloc] initWithData:data];
  }

  return nil;
}

+ (nullable instancetype)
createResponseForRequest:(ElectrodeBridgeRequest *)request
        withResponseData:(nullable id)data
      withFailureMessage:(nullable id<ElectrodeFailureMessage>)failureMessage {
  return
      [[ElectrodeBridgeResponse alloc] initWithName:request.name
                                          messageId:request.messageId
                                               type:ElectrodeMessageTypeResponse
                                               data:data
                                     failureMessage:failureMessage];
}

- (nullable instancetype)initWithData:(NSDictionary *)data {
  if (self = [super initWithData:data]) {
    NSDictionary *error = [data objectForKey:kElectrodeBridgeResponseError];
    if (error != nil &&
        [error isKindOfClass:[NSDictionary class]]) { // check the
                                                      // arguemntsEx.toBundle
                                                      // thingy
      NSString *code =
          (NSString *)[error objectForKey:kElectrodeBridgeResponseErrorCode];
      NSString *message =
          (NSString *)[error objectForKey:kElectrodeBridgeResponseErrorMessage];
      _failureMessage = [ElectrodeBridgeFailureMessage
          createFailureMessageWithCode:
              (code != nil ? code : kElectrodeBridgeResponseUnknownErrorCode)
                               message:(message != nil ? message
                                                       : @"unknown error")];
    }
  }

  return self;
}

- (nullable instancetype)initWithName:(NSString *)name
                            messageId:(NSString *)messageId
                                 type:(ElectrodeMessageType)type
                                 data:(id)data
                       failureMessage:
                           (id<ElectrodeFailureMessage>)failureMessage {
  if (self =
          [super initWithName:name messageId:messageId type:type data:data]) {
    _failureMessage = failureMessage;
  }

  return self;
}

- (NSDictionary *)toDictionary {
  if (_failureMessage) {
    NSMutableDictionary *messageDict =
        [[NSMutableDictionary alloc] initWithDictionary:[super toDictionary]];
    NSMutableDictionary *errorDict = [[NSMutableDictionary alloc] init];
    [errorDict setObject:_failureMessage.message
                  forKey:kElectrodeBridgeResponseErrorMessage];
    [errorDict setObject:_failureMessage.code
                  forKey:kElectrodeBridgeResponseErrorCode];
    [messageDict setObject:errorDict forKey:kElectrodeBridgeResponseError];
    return messageDict;
  }
  return [super toDictionary];
}

- (NSString *)description {
  return [NSString stringWithFormat:@"%@, failureMessage:%@",
                                    [super description], self.failureMessage];
}

@end
