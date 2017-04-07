//
//  ElectrodeBridgeBaseTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/27/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeBaseTests.h"
#import "ElectrodeBridgeTransceiver_Internal.h"
#import "ElectrodeBridgeMessage.h"
#import "ElectrodeBridgeEventNew.h"

@implementation ElectrodeBridgeRequestNew (CustomeBuilder)

+ (instancetype)createRequestWithName:(NSString *)name
{
    ElectrodeBridgeRequestNew *request = [[ElectrodeBridgeRequestNew alloc] initWithName:name data:nil];
    return request;
}

+ (instancetype)createRequestWithName:(NSString *)name data:(nullable id)data
{
    ElectrodeBridgeRequestNew *request = [[ElectrodeBridgeRequestNew alloc] initWithName:name data:data];
    return request;
}

@end


@implementation ElectrodeBridgeEventNew (ElectrodeBridgeEventNewAddition)

+ (nonnull instancetype)createEventWithName:(nonnull NSString *)name data:(nullable id)eventData {
    ElectrodeBridgeEventNew *event = [[ElectrodeBridgeEventNew alloc] initWithName:name data:eventData];
    return event;
}
@end

@implementation MockElectrodeBridgeEventListener

- (_Nonnull instancetype)initWithonEventBlock:(nullable onEventBlock)eventBlock {
    if (self = [super init]) {
        self.eventBlock = eventBlock;
    }
    return self;
}

- (void)onEvent:(id)eventPayload {
    self.eventBlock(eventPayload);
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
    MockBridgeTransceiver *mockTransceiver = [[MockBridgeTransceiver alloc] init];
    [MockBridgeTransceiver createWithTransceiver:mockTransceiver];
    mockTransceiver.myMockListenerStore = self.mockListenerStore;
    return @[mockTransceiver];
}

-(void)initializeBundle
{
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:nil];
    self.bridge = bridge;
    ElectrodeBridgeTransceiver *transceiver = (ElectrodeBridgeTransceiver *) [self getNativeBridge];
    [transceiver onReactNativeInitialized];
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

- (NSDictionary *)createBridgeRequestForName:(NSString *)name id:(NSString *)requestId data:(id)data
{
    NSMutableDictionary *jsRequest = [[NSMutableDictionary alloc] init];
    [jsRequest setObject:name forKey:kElectrodeBridgeMessageName];
    [jsRequest setObject:requestId forKey:kElectrodeBridgeMessageId];
    [jsRequest setObject:[ElectrodeBridgeMessage convertEnumTypeToString:ElectrodeMessageTypeRequest] forKey:kElectrodeBridgeMessageType];
    if(data)
    {
        [jsRequest setObject:data forKey:kElectrodeBridgeMessageData];
    }
    return jsRequest;
}


- (NSDictionary *)createResponseDataWithName:(NSString *)name id:(NSString *)responseId data:(id)data {
    NSString* responseName = name;
    NSMutableDictionary* mockResponse = [[NSMutableDictionary alloc] init];
    [mockResponse setObject:responseName forKey:kElectrodeBridgeMessageName];
    [mockResponse setObject:responseId forKey:kElectrodeBridgeMessageId];
    [mockResponse setObject:[ElectrodeBridgeMessage convertEnumTypeToString:ElectrodeMessageTypeResponse] forKey:kElectrodeBridgeMessageType];
    if (data) {
        [mockResponse setObject:data forKey:kElectrodeBridgeMessageData];
    }
    return mockResponse;
}

@end

@implementation MockBridgeTransceiver

-(instancetype)init {
    if(self = [super init]) {

    }

    return self;
}

+ (NSArray *)electrodeModules
{
    return @[[MockBridgeTransceiver sharedInstance]];
}


- (void)emitMessage:(ElectrodeBridgeMessage *)bridgeMessage
{
    NSLog(@"Trying to emit messgae to JS, mock JS implemenation here.");
    MockJSEeventListener* registeredListener = [self.myMockListenerStore objectForKey:bridgeMessage.name];
    if(registeredListener) {
        switch (bridgeMessage.type) {
            case ElectrodeMessageTypeEvent:
                if(!registeredListener.evetBlock)
                {
                    NSLog(@"TEST FAILURE: expected a non null event block but found a nil event block for mock listener registered for: %@", bridgeMessage.name);
                }
                else
                {
                    registeredListener.evetBlock((ElectrodeBridgeEventNew *)bridgeMessage);
                }
                break;
            case ElectrodeMessageTypeRequest:
                if(!registeredListener.requestBlock)
                {
                    NSLog(@"TEST FAILURE: expected a non null request block but found a nil request block for mock listener registered for : %@", bridgeMessage.name);
                }
                else
                {
                    registeredListener.requestBlock((ElectrodeBridgeRequestNew *)bridgeMessage);
                }
                
                if (registeredListener.response) {
                    NSMutableDictionary *response = [[NSMutableDictionary alloc] initWithDictionary:registeredListener.response];
                    [response setObject:bridgeMessage.messageId forKey:kElectrodeBridgeMessageId];
                    [self sendMessage: response];
                }
                break;
            case ElectrodeMessageTypeResponse:
                if(!registeredListener.responseBlock)
                {
                    NSLog(@"TEST FAILURE: expected a non null response block but found a nil response block for mock listener registered for: %@", bridgeMessage.name);
                }
                else
                {
                    registeredListener.responseBlock((ElectrodeBridgeResponse *)bridgeMessage);
                }
                break;
            default:
                NSLog(@"TEST FAILURE: Should never reach here for request:%@", bridgeMessage.name);
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

-(nonnull instancetype) initWithRequestBlock: (nonnull requestBlock) requestBlock
                                    response:(nonnull NSDictionary *)response {
    if (self = [super init]) {
        self.requestBlock = requestBlock;
        self.response = response;
    }
    
    return self;
}
@end
