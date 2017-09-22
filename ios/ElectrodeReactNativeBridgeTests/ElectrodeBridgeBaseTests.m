/*
 * Copyright 2017 WalmartLabs
 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "ElectrodeBridgeBaseTests.h"
#import "ElectrodeBridgeMessage.h"
#import "ElectrodeBridgeEvent.h"
#import "ElectrodeLogger.h"
#import "ElectrodeBridgeHolder.h"

@implementation ElectrodeBridgeRequest (CustomeBuilder)

+ (instancetype)createRequestWithName:(NSString *)name
{
    ElectrodeBridgeRequest *request = [[ElectrodeBridgeRequest alloc] initWithName:name data:nil];
    return request;
}

+ (instancetype)createRequestWithName:(NSString *)name data:(nullable id)data
{
    ElectrodeBridgeRequest *request = [[ElectrodeBridgeRequest alloc] initWithName:name data:data];
    return request;
}

@end


@implementation ElectrodeBridgeEvent (ElectrodeBridgeEventAddition)

+ (nonnull instancetype)createEventWithName:(nonnull NSString *)name data:(nullable id)eventData {
    ElectrodeBridgeEvent *event = [[ElectrodeBridgeEvent alloc] initWithName:name data:eventData];
    return event;
}
@end

@interface ElectrodeBridgeBaseTests ()
@property(nonatomic, strong) RCTBridge *bridge;
@property(nonatomic, strong) MockBridgeTransceiver *transceiver;
@end

@implementation ElectrodeBridgeBaseTests

-(void)setUp
{
    [self initializeBundle];
    [ElectrodeBridgeHolder initialize];
    [self.transceiver onTransceiverModuleInitialized];
    [self.transceiver onReactNativeInitialized];
    ElectrodeConsoleLogger.sharedInstance.logLevel = ElectrodeLogLevelVerbose;
}

-(void)tearDown {
    [self.transceiver resetRegistrar];

}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
    return [self allJSBundleFiles][0];
}

-(void)initializeBundle
{
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self launchOptions:nil];
    self.bridge = bridge;
    MockBridgeTransceiver *transceiver = (MockBridgeTransceiver *) [self getNativeBridge];
    self.transceiver = transceiver;
}

- (id<ElectrodeNativeBridge>)getNativeBridge {
    id <ElectrodeNativeBridge> nativeBridge = [self.bridge moduleForClass:[MockBridgeTransceiver class]];
    XCTAssertNotNil(nativeBridge, @"Native bridge instance is nil");
    return nativeBridge;
}


- (NSArray *)allJSBundleFiles
{
    NSArray *returnFiles = nil;
    NSURL *bundle = [[NSBundle bundleForClass:self.class] bundleURL];
    NSError *error = nil;
    
    NSArray *files =
    [[NSFileManager defaultManager] contentsOfDirectoryAtURL:bundle
                                  includingPropertiesForKeys:nil
                                                     options:NSDirectoryEnumerationSkipsHiddenFiles
                                                       error:&error];
    if (!error)
    {
        NSPredicate *jsBundlePredicate = [NSPredicate predicateWithFormat:@"pathExtension='jsbundle'"];
        returnFiles = [files filteredArrayUsingPredicate:jsBundlePredicate];
    }
    return returnFiles;
}


- (id<ElectrodeReactBridge>)getReactBridge {
    id <ElectrodeReactBridge> reactBridge = [self.bridge moduleForClass:[MockBridgeTransceiver class]];
    XCTAssertNotNil(reactBridge, @"React bridge instance is nil");
    return reactBridge;
}

- (void) addMockEventListener:(MockJSEeventListener *) mockJsEventListener forName:(NSString *)name {
    [[MockBridgeTransceiver sharedInstance].myMockListenerStore setValue: mockJsEventListener forKey:name];
    NSLog(@"Mock Listener Store = %@", [MockBridgeTransceiver sharedInstance].myMockListenerStore);
}

- (void) appendMockEventListener:(MockJSEeventListener *)mockJsEventListener forName:(NSString *)name {
    [[MockBridgeTransceiver sharedInstance].myMockListenerStore setValue: mockJsEventListener forKey:name];
}

- (void)removeMockEventListenerWithName: (NSString *)name {
    [[MockBridgeTransceiver sharedInstance].myMockListenerStore removeObjectForKey:name];
}

- (NSDictionary *)createBridgeRequestForName:(NSString *)name id:(NSString *)requestId data:(id)data {
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

- (nonnull NSDictionary *)createEventDataWithName:(nonnull NSString *)eventName id:(nonnull NSString *)eventId data:(nullable id)eventData {
    NSMutableDictionary* mockEvent = [NSMutableDictionary new];
    [mockEvent setObject:eventName forKey:kElectrodeBridgeMessageName];
    [mockEvent setObject:eventId forKey:kElectrodeBridgeMessageId];
    [mockEvent setObject:[ElectrodeBridgeMessage convertEnumTypeToString:ElectrodeMessageTypeEvent] forKey:kElectrodeBridgeMessageType];
    if (eventData) {
        [mockEvent setObject:eventData forKey:kElectrodeBridgeMessageData];
    }
    return mockEvent;
}
@end


@implementation MockBridgeTransceiver

-(instancetype) init {
    if (self = [super init]) {
        _myMockListenerStore = [[NSMutableDictionary alloc] init];
    }
    
    return self;
}
RCT_EXPORT_MODULE();

+ (NSArray *)electrodeModules
{
    return @[[[MockBridgeTransceiver alloc] init]];
}

-(NSArray *) supportedEvents
{
    return @[@"electrode.bridge.message"];
}

- (void)emitMessage:(ElectrodeBridgeMessage *)bridgeMessage
{
    NSLog(@"Trying to emit messgae to JS, mock JS implemenation here.");
    MockJSEeventListener* registeredListener = [self.myMockListenerStore objectForKey:bridgeMessage.name];
    
    if(registeredListener) {
        switch (bridgeMessage.type) {
            case ElectrodeMessageTypeEvent:
                if(!registeredListener.jSCallBackBlock)
                {
                    NSLog(@"TEST FAILURE: expected a non null event block but found a nil event block for mock listener registered for: %@", bridgeMessage.name);
                }
                else
                {
                    registeredListener.jSCallBackBlock((NSDictionary *)[bridgeMessage toDictionary]);
                }
                break;
            case ElectrodeMessageTypeRequest:
                if(!registeredListener.jSCallBackBlock)
                {
                    NSLog(@"TEST FAILURE: expected a non null request block but found a nil request block for mock listener registered for : %@", bridgeMessage.name);
                }
                else
                {
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 4 * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
                        registeredListener.jSCallBackBlock((NSDictionary *)[bridgeMessage toDictionary]);
                        if (registeredListener.response) {
                            NSMutableDictionary *response = [[NSMutableDictionary alloc] init];
                            [response setObject:registeredListener.response forKey:kElectrodeBridgeMessageData];
                            [response setObject:bridgeMessage.messageId forKey:kElectrodeBridgeMessageId];
                            [response setObject:kElectrodeBridgeMessageResponse forKey:kElectrodeBridgeMessageType];
                            [response setObject:bridgeMessage.name forKey:kElectrodeBridgeMessageName];
                            [self sendMessage: response];
                        }
                    });
                }
                
                break;
            case ElectrodeMessageTypeResponse:
                if(!registeredListener.jSCallBackBlock)
                {
                    NSLog(@"TEST FAILURE: expected a non null response block but found a nil response block for mock listener registered for: %@", bridgeMessage.name);
                }
                else
                {
                    registeredListener.jSCallBackBlock((NSDictionary *)[bridgeMessage toDictionary]);
                }
                break;
            default:
                NSLog(@"TEST FAILURE: Should never reach here for request:%@", bridgeMessage.name);
                break;
        }
    }
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

-(nonnull instancetype) initWithjSBlock: (ElectrodeBaseJSBlock) jSBlock{
    if(self = [super init]) {
        self.jSCallBackBlock = jSBlock;
    }
    return self;
}

-(instancetype) initWithjSBlock:(ElectrodeBaseJSBlock)jSBlock response: (id) response {
    if(self = [super init]) {
        self.jSCallBackBlock = jSBlock;
        self.response = response;
    }
    return self;
}
@end
