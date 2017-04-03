//
//  ElectrodeBridgeBaseTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/27/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeBaseTests.h"
#import "ElectrodeBridgeTransceiver_Internal.h"

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


+ (instancetype)createRequestWithName:(NSString *)name data:(id)data
{
    NSString * const kElectrodeBridgeMessageName = @"name";
    NSString * const kElectrodeBridgeMessageId = @"id";
    NSString * const kElectrodeBridgeMessageType = @"type";
    NSString * const kElectrodeBridgeMessageData = @"data";
    NSDictionary *requestData = @{kElectrodeBridgeMessageName: name,
                           kElectrodeBridgeMessageId:@"1234",
                           kElectrodeBridgeMessageType:kElectrodeBridgeMessageRequest,
                           kElectrodeBridgeMessageData: data
                           };
    ElectrodeBridgeRequestNew *request = [ElectrodeBridgeRequestNew createRequestWithData:requestData];
    
    return request;

}

@end

@implementation MockElectrodeBridgeResponseListener


- (instancetype)initWithExpectation:(XCTestExpectation *)expectation successBlock:(successBlock)success
{
    if(self = [super init]) {
        self.successBlk = success;
        self.isSuccessListener = YES;
        self.expectation = expectation;
        return self;
    }
    return nil;
}

-(instancetype)initWithExpectation:(XCTestExpectation *)expectation failureBlock:(failureBlock)failure
{
    if(self = [super init]) {
        self.failureBlk = failure;
        self.isSuccessListener = NO;
        self.expectation = expectation;
        return self;
    }
    return nil;
}

-(void)onFailure:(id<ElectrodeFailureMessage>)failureMessage
{
    if(!self.isSuccessListener) {
        self.failureBlk(failureMessage);
    } else {
        XCTFail("Expected a success response");
    }
}
-(void)onSuccess:(nullable NSDictionary *)responseData
{
    if(self.isSuccessListener) {
        self.successBlk(responseData);
    } else {
        XCTFail("Expected a failure response");
    }
}

@end


@interface ElectrodeBridgeBaseTests ()
@property(nonatomic, strong) RCTBridge *bridge;
@end

@implementation ElectrodeBridgeBaseTests

-(void)setUp
{
    [self initializeBundle];
    
    if(self.mockListenerStore) {
        [self.mockListenerStore removeAllObjects];
    }
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
    NSString *absolutePath = @"/Users/w0l00qx/Code/react-native-electrode-bridge/ios/ElectrodeReactNativeBridge/ElectrodeReactNativeBridgeTests/MiniApp.jsbundle";
    
    return [[NSURL alloc] initWithString:absolutePath];
}

- (NSArray<id<RCTBridgeModule>> *)extraModulesForBridge:(RCTBridge *)bridge
{
    self.mockListenerStore = [[NSMutableDictionary alloc] init];
    return @[[[MockBridgeTransceiver alloc] initWithJsMockListenerStore:self.mockListenerStore]];
}

-(void)initializeBundle
{
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:nil];
    self.bridge = bridge;
}

-(id<ElectrodeNativeBridge>)getNativeBridge
{
    return [self.bridge moduleForClass:[MockBridgeTransceiver class]];
}

- (id<ElectrodeReactBridge>)getReactBridge
{
    return [self.bridge moduleForClass:[MockBridgeTransceiver class]];
}

-(void) addMockEventListener:(MockJSEeventListener *) mockJsEventListener forName:(NSString *)name
{
    [_mockListenerStore setValue:mockJsEventListener forKey:name];
}

@end


@implementation MockBridgeTransceiver

- (instancetype)initWithJsMockListenerStore:(NSMutableDictionary<NSString *,MockJSEeventListener *> *)mockListenerStore
{
    if(self = [super init])
    {
        self.mockListenerStore = mockListenerStore;
    }
    
    return self;
}

- (void)emitMessage:(ElectrodeBridgeMessage *)bridgeMessage
{
    NSLog(@"Trying to emit messgae to JS, mock JS implemenation here.");
    MockJSEeventListener* registeredListener = [self.mockListenerStore objectForKey:bridgeMessage.name];
    if(registeredListener) {
        switch (bridgeMessage.type) {
            case ElectrodeMessageTypeEvent:
                registeredListener.evetBlock((ElectrodeBridgeEventNew *)bridgeMessage);
                break;
            case ElectrodeMessageTypeRequest:
                registeredListener.requestBlock((ElectrodeBridgeRequestNew *)bridgeMessage);
                break;
            case ElectrodeMessageTypeResponse:
                registeredListener.responseBlock((ElectrodeBridgeResponse *)bridgeMessage);
                break;
            default:
                XCTFail("Should never reach here");
                break;
        }
    }
    
}

- (NSArray<NSString *> *)supportedEvents
{
     return @[@"bridgeTest"];
}

@end


@implementation MockJSEeventListener

- (instancetype)initWithEventBlock:(evetBlock)evetBlock
{
    if(self = [super init]) {
        self.evetBlock = evetBlock;
    }
    return self;
}

- (instancetype)initWithRequestBlock:(requestBlock)requestBlock
{
    if(self = [super init]) {
        self.requestBlock = requestBlock;
    }
    return self;
}

- (instancetype)initWithResponseBlock:(responseBlock)responseBlock
{
    if(self = [super init]) {
        self.responseBlock = responseBlock;
    }
    return self;
}


@end
