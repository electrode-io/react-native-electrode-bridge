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

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeBaseTests.h"
#import "ElectrodeBridgeResponse.h"
#import "Person.h"
#import "ElectrodeBridgeHolder.h"


@interface MockConstantProvider : XCTestCase <ConstantsProvider>
- (instancetype)initWithConstant:(NSDictionary *)constant;
@property (nonatomic, strong) NSDictionary* constant;
@end

@implementation MockConstantProvider

- (instancetype)initWithConstant:(NSDictionary *)constant {
    self = [super init];
    if (self) {
        self.constant = constant;
    }
    return self;
}

- (NSDictionary<NSString *,id> *)constantsToExport {
    return self.constant;
}

@end


@interface ElectrodeTransceiverTests : ElectrodeBridgeBaseTests <ElectrodeFailureMessage>

@end

@implementation ElectrodeTransceiverTests

- (XCTestExpectation*)createExpectationWithDescription:(nullable NSString*)description {
    return [self expectationWithDescription:description];
}

- (void)waitForExpectationToFullFillOrTimeOut {
    [self waitForExpectationsWithTimeout:10.0 handler:^(NSError * _Nullable error) {
        if (error) {
            XCTFail(@"timed out");
        }
    }];
}

- (void)testSendTimeOutRequest {
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendTimeOutRequest"];
    id<ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* const name = @"testTimeOutRequest";
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithName:name];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageName], name);
    }] forName:@"testTimeOutRequest"];
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssertNil(data);
        XCTAssertNotNil(message);
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

//create event listener with response.
- (void)testSendRequestWithRequestDataAndExpectStringResponseFromNativeToJS {
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendRequestWithRequestDataAndExpectStringResponseFromNativeToJS"];
    NSString* const name = @"sendRequest";
    NSString* const sendResponseString = @"responseString";
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithName:name data:@"requestString"];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageName], name);
    } response:sendResponseString] forName:name];
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssertEqual(data, sendResponseString);
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendRequestWithRequestDataAndExpectDictionaryResponseFromNativeToJS {
  __weak __block  XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendRequestWithRequestDataAndExpectStringResponseFromNativeToJS"];
    NSString* const name = @"sendRequest";
    NSDictionary* const sendResponseDictionary = @{@"response" : @"responseDictionary"};
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithName:name data:@"requestDictionary"];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageName], name);
    } response:sendResponseDictionary] forName:name];
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssertEqual(data, sendResponseDictionary);
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendRequestWithPrimitiveTypeResponseFromNativeToJS {
  __weak __block  XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendRequestWithPrimitiveTypeResponseFromNativeToJS"];
    NSString* const name = @"sendRequest";
    int integer = 2;
    NSNumber* number = [NSNumber numberWithInt:integer];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithName:name data:@"requestPrimitiveType"];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageName], name);
    } response:number] forName:name];
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssertEqual(data, number);
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendRequestWithBooleanFromNativeToJS {
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendRequestWithBooleanFromNativeToJS"];
    NSString* const name = @"sendRequest";
    BOOL isYes = YES;
    NSNumber* boolean = [NSNumber numberWithBool:isYes];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithName:name data:@"requestBoolean"];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageName], name);
    } response:boolean] forName:name];
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssertEqual([data boolValue], isYes);
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendRequestWithComplexDataFromNativeToJS {
    __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendRequestWithComplexDataFromNativeToJS"];
    NSString* const name = @"sendRequest";
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSDictionary* attributes = @{
                                 @"firstname" : @"Mo",
                                 @"lastname"  : @"Abhi",
                                 @"gender"    : @"male",
                                 @"age"       : @"28",
                                 @"company"   : @"xyz"
                                 };
    Person* person = [[Person alloc]  initWithAttributes:attributes];
    XCTAssertNotNil(person, @"Error in creating person object");
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithName:name data:@"requestComplexData"];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageName], name);
    } response:person] forName:name];
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        Person* response = (Person*) data;
        NSLog(@"First name  %@", [[response attributes] objectForKey:@"firstname"]);
        XCTAssertNotNil(response, @"Response is nil. There's an error in receiving response");
        XCTAssertEqual([[person attributes] objectForKey:@"firstname"], [[response attributes] objectForKey:@"firstname"] , @"Sent and received response data doesn't match");
        XCTAssertEqual([[person attributes] objectForKey:@"lastname"], [[response attributes] objectForKey:@"lastname"] , @"Sent and received response data doesn't match");
        XCTAssertEqual([[person attributes] objectForKey:@"gender"], [[response attributes] objectForKey:@"gender"] , @"Sent and received response data doesn't match");
        XCTAssertEqual([[person attributes] objectForKey:@"age"], [[response attributes] objectForKey:@"age"] , @"Sent and received response data doesn't match");
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendRequestWithEmptyRequestDataAndNEmptyResponseNativeToNative {
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSampleRequestNativeToNative"];
    id<ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* const name = @"sendRequest";
    NSString* emptyRequestData = @"";
    ElectrodeBridgeRequest *request = [ElectrodeBridgeRequest createRequestWithName:name data:emptyRequestData];
    NSUUID *uuid = [NSUUID UUID];

    [nativeBridge registerRequestCompletionHandlerWithName:name uuid:uuid completion:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        block(data, nil);
    }];
    
    [nativeBridge sendRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssertEqual(data, emptyRequestData, @"error");
        [expectation fulfill];
        expectation = nil;
    }];
    [self waitForExpectationToFullFillOrTimeOut];
}

- (void)testSendRequestWithEmptyRequestDataAndNEmptyResponseJSToNative {
  __weak __block  XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSampleRequestNativeToNative"];
    NSString* const name = @"sendRequest";
    NSString* emptyRequestData = @"";
    id<ElectrodeReactBridge> reactBridge = [self getReactBridge];
    id<ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSUUID *uuid = [NSUUID UUID];
    
    [nativeBridge registerRequestCompletionHandlerWithName:name uuid:uuid completion:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        block(data, nil);
        
    }];
    
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageData], emptyRequestData);
        [expectation fulfill];
    }] forName:name];
    NSDictionary *jsRequest = [self createBridgeRequestForName:name id:[ElectrodeBridgeMessage UUID] data:emptyRequestData];
    [reactBridge sendMessage:jsRequest];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendRequestToGetArrayFromRCTToNative {
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendRequestToGetArrayFromJsToNative"];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    id <ElectrodeReactBridge> reactBridge = [self getReactBridge];
    NSArray* testArray = @[@"apple", @"mango", @"orange"];
    NSString* const name = @"sendRequest";
    NSUUID *uuid = [NSUUID UUID];

    [nativeBridge registerRequestCompletionHandlerWithName:name uuid:uuid completion:^(id  _Nullable data, ElectrodeBridgeResponseCompletionHandler  _Nonnull block) {
        block(data, nil);

    }];
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([result valueForKey:kElectrodeBridgeMessageData], testArray);
        [expectation fulfill];
    }] forName:name];
    NSDictionary *jsRequest = [self createBridgeRequestForName:name id:[ElectrodeBridgeMessage UUID] data:testArray];
    [reactBridge sendMessage:jsRequest];
    [self waitForExpectationToFullFillOrTimeOut];
    [self removeMockEventListenerWithName:name];
}

- (void)testSendEventFromNativeToRCT {
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* testEventName = @"com.walmart.ern.testevent";
    NSString* testData = @"testeventdata";
    ElectrodeBridgeEvent* event = [ElectrodeBridgeEvent createEventWithName:testEventName data:testData];
    XCTAssertNotNil(event, @"Event instance is nil");
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual(testData, [result valueForKey:kElectrodeBridgeMessageData]);
        XCTAssertEqual(testEventName, event.name, @"Not an event RCT expected!");
        XCTAssertEqual(testData, event.data, @"Not an event RCT expected!");
    }] forName:testEventName];
    [nativeBridge sendEvent:event];
}

- (void)testSendEventWithComplexDataFromNativeToRCT {
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* testEventName = @"com.walmart.ern.testevent";
    NSDictionary* data = @{
                           @"string" : @"stringValue",
                           @"integer" : [NSNumber numberWithFloat:18.333333],
                           @"array" : [NSArray arrayWithObjects:@"1", @"2",@"3", nil],
                           @"dictionary" : @{
                                   @"key" : @"value"
                                   }
                           };
    ElectrodeBridgeEvent* event = [ElectrodeBridgeEvent createEventWithName:testEventName data:data];
    XCTAssertNotNil(event, @"Event instance is nil");
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithjSBlock:^(NSDictionary * _Nonnull result) {
        XCTAssertEqual([[data objectForKey:@"array"] firstObject], [[[result objectForKey:kElectrodeBridgeMessageData] objectForKey:@"array"] firstObject]);
        XCTAssertEqual([data objectForKey:@"string"] , [[result objectForKey:kElectrodeBridgeMessageData] objectForKey:@"string"]);
        XCTAssertEqual([data objectForKey:@"integer"] , [[result objectForKey:kElectrodeBridgeMessageData] objectForKey:@"integer"]);
        XCTAssertEqual(testEventName, event.name, @"Not an event RCT expected!");
        XCTAssertEqual(data, event.data, @"Not an event RCT expected!");
    }] forName:testEventName];
    [nativeBridge sendEvent:event];
}


- (void)testSendEventFromNativeToNative {
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"waitfornativeeventtocomplete"];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* testEventName = @"com.walmart.ern.nativetonativeevent";
    NSString* data = @"nativeeventdata";
    ElectrodeBridgeEvent* event = [ElectrodeBridgeEvent createEventWithName:testEventName data:data];
    NSUUID *uuid = [NSUUID UUID];
    
    [nativeBridge registerEventListenerWithName:testEventName uuid:uuid listener:^(id  _Nullable payLoad) {
        NSLog(@"Event result = %@", payLoad);
        XCTAssertEqual(payLoad, data, @"Failure, received a different event!!");
        [expectation fulfill];
        expectation = nil;
    }];
    
    //then dispatch an event to the native
    [nativeBridge sendEvent:event];
    [self waitForExpectationToFullFillOrTimeOut];
}

- (void)testSendEventWithSimpleDataFromRCTToNative {
    id <ElectrodeReactBridge> reactBridge = [self getReactBridge];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* testEventName = @"com.walmart.ern.reacttonativeevent1";
    NSString* data = @"reacteventdata";
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"waitforreacteventtocomplete"];
    ElectrodeBridgeEvent* event = [ElectrodeBridgeEvent createEventWithName:testEventName data:data];
    XCTAssertNotNil(event, @"event instance is nil");
    NSUUID *uuid = [NSUUID UUID];
    [nativeBridge registerEventListenerWithName:testEventName uuid:uuid listener:^(id  _Nullable payLoad) {
        XCTAssertEqual(payLoad, data, @"Failure, received a different event!!");
        [expectation fulfill];
        expectation = nil;
    }];
    NSDictionary* eventMessage = [self createEventDataWithName:testEventName id:[ElectrodeBridgeMessage UUID] data:data];
    [reactBridge sendMessage:eventMessage];
    [self waitForExpectationToFullFillOrTimeOut];
}

- (void)testSendEventWithComplexDataFromRCTToNative {
    id <ElectrodeReactBridge> reactBridge = [self getReactBridge];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    NSString* testEventName = @"com.walmart.ern.reacttonativeevent";
    NSDictionary* data = @{
                           @"string" : @"stringValue",
                           @"integer" : [NSNumber numberWithFloat:18.333333],
                           @"array" : [NSArray arrayWithObjects:@"1", @"2",@"3", nil],
                           @"dictionary" : @{
                                   @"key" : @"value"
                                   }
                           };
   __weak __block XCTestExpectation* expectation = [self createExpectationWithDescription:@"waitforreacteventtocomplete"];
    ElectrodeBridgeEvent* event = [ElectrodeBridgeEvent createEventWithName:testEventName data:data];
    XCTAssertNotNil(event, @"event instance is nil");

    
    NSUUID *uuid = [NSUUID UUID];
    [nativeBridge registerEventListenerWithName:testEventName uuid:uuid listener:^(id  _Nullable payLoad) {
        XCTAssertEqual([[data objectForKey:@"array"] firstObject], [[payLoad objectForKey:@"array"] firstObject]);
        XCTAssertEqual([data objectForKey:@"string"] , [payLoad objectForKey:@"string"]);
        XCTAssertEqual([data objectForKey:@"integer"] , [payLoad objectForKey:@"integer"]);
        XCTAssertEqual(testEventName, event.name, @"Not an event RCT expected!");
        XCTAssertEqual(data, event.data, @"Not an event RCT expected!");
        XCTAssertEqual(payLoad, data, @"Failure, received a different event!!");
        [expectation fulfill];
        expectation = nil;
    }];
    NSDictionary* eventMessage = [self createEventDataWithName:testEventName id:[ElectrodeBridgeMessage UUID] data:data];
    [reactBridge sendMessage:eventMessage];
    [self waitForExpectationToFullFillOrTimeOut];
}

- (void)testConstantsThruTransceiver {
    NSMutableDictionary* constantsConcatenate = [NSMutableDictionary new];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    MockConstantProvider* mockConstantProvider = [[MockConstantProvider alloc] initWithConstant:@{@"key1" : @"value1"}];
    [constantsConcatenate addEntriesFromDictionary:mockConstantProvider.constant];
    [nativeBridge addConstantsProvider:mockConstantProvider];
    MockConstantProvider* testConstantProvider = [[MockConstantProvider alloc] initWithConstant:@{@"key2" : @"value2"}];
    [nativeBridge addConstantsProvider:testConstantProvider];
    [constantsConcatenate addEntriesFromDictionary:testConstantProvider.constant];
    NSDictionary* constantsExported =  [((id<RCTBridgeModule>)nativeBridge) constantsToExport];
    XCTAssertTrue([constantsExported isEqualToDictionary:constantsConcatenate]);
}

- (void)testConstantsThruBridgeHolder {
    NSMutableDictionary* constantsConcatenate = [NSMutableDictionary new];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    MockConstantProvider* mockConstantProvider = [[MockConstantProvider alloc] initWithConstant:@{@"key1" : @"value1"}];
    [constantsConcatenate addEntriesFromDictionary:mockConstantProvider.constant];
    [ElectrodeBridgeHolder addConstantsProvider:mockConstantProvider];
    MockConstantProvider* testConstantProvider = [[MockConstantProvider alloc] initWithConstant:@{@"key2" : @"value2"}];
    [ElectrodeBridgeHolder addConstantsProvider:testConstantProvider];
    [constantsConcatenate addEntriesFromDictionary:testConstantProvider.constant];
    NSDictionary* constantsExported =  [((id<RCTBridgeModule>)nativeBridge) constantsToExport];
    XCTAssertTrue([constantsExported isEqualToDictionary:constantsConcatenate]);
}

@end
