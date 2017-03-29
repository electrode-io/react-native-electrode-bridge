//
//  ElectrodeBridgeBaseTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/27/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeBaseTests.h"

@implementation ElectrodeBridgeRequestNew (CustomeBuilder)

+(instancetype)createRequestWithName: (NSString *)name
{
    NSString * const kElectrodeBridgeMessageName = @"name";
    NSString * const kElectrodeBridgeMessageId = @"id";
    NSString * const kElectrodeBridgeMessageType = @"type";
    NSString * const kElectrodeBridgeMessageData = @"data";
    NSDictionary *data = @{kElectrodeBridgeMessageName: name,
                           kElectrodeBridgeMessageId:@"1234",
                           kElectrodeBridgeMessageType:kElectrodeBridgeMessageRequest,
                               kElectrodeBridgeMessageData: @{@"key": @"value"}
                           };
    ElectrodeBridgeRequestNew *request = [ElectrodeBridgeRequestNew createRequestWithData:data];
    
    return request;
}


@end

@implementation MockElectrodeBridgeResponseListener

-(void)onFailure:(id<ElectrodeFailureMessage>)failureMessage
{
    if(!self.isSuccessListener) {
        [self.expectation fulfill];
    }
}
-(void)onSuccess:(nullable NSDictionary *)responseData
{
    if(self.isSuccessListener) {
        [self.expectation fulfill];
    }
}

@end


@interface ElectrodeBridgeBaseTests ()
@property(nonatomic, strong) RCTBridge *bridge;
@end

@implementation ElectrodeBridgeBaseTests
- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
    NSString *absolutePath = @"/Users/w0l00qx/Code/react-native-electrode-bridge/ios/ElectrodeReactNativeBridge/ElectrodeReactNativeBridgeTests/MiniApp.jsbundle";
    
    return [[NSURL alloc] initWithString:absolutePath];
}

- (NSArray<id<RCTBridgeModule>> *)extraModulesForBridge:(RCTBridge *)bridge
{
    return @[[[ElectrodeBridgeTransceiver alloc] init]];
}

-(void)initializeBundle
{
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:nil];
    self.bridge = bridge;
}

@end

