//
//  ElectrodeBridgeTransceiverTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/27/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeBaseTests.h"
#import "TestRequestHandler.h"
#import "ElectrodeBridgeResponse.h"

@interface ElectrodeBridgeTransceiverTests : ElectrodeBridgeBaseTests

@end

@implementation ElectrodeBridgeTransceiverTests

-(void)testSendTimeOutRequest
{
    XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSendTimeOutRequest"];
    
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithRequestBlock:^(ElectrodeBridgeRequestNew *request) {
        XCTAssertNotNil(request);
    }] forName:@"test1"];
    
    id<ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    ElectrodeBridgeRequestNew *request = [ElectrodeBridgeRequestNew createRequestWithName:@"test1"];
    MockElectrodeBridgeResponseListener *listener = [[MockElectrodeBridgeResponseListener alloc] initWithExpectation:expectation failureBlock:^(id failureMessage) {
        XCTAssertNotNil(failureMessage);
        [expectation fulfill];
    }];
    
    [nativeBridge sendRequest:request withResponseListener:listener];
    
    
    [self waitForExpectationToFullFillOrTimeOut];

}

-(void)testSendRequestWithEmptyRequestDataAndNEmptyResponseNativeToNative
{
    XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSampleRequestNativeToNative"];
    id<ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    
    ElectrodeBridgeRequestNew *request = [ElectrodeBridgeRequestNew createRequestWithName:@"testRequest" data:nil];
    
    [nativeBridge regiesterRequestHandlerWithName:@"testRequest" handler:[[TestRequestHandler alloc] initWithOnRequestBlock:^(NSDictionary *data, id<ElectrodeBridgeResponseListener> responseListener) {
        XCTAssertNil(data);
        [responseListener onSuccess:nil];
    }] error:nil];
    
    
    MockElectrodeBridgeResponseListener *listener = [[MockElectrodeBridgeResponseListener alloc] initWithExpectation:expectation successBlock:^(NSDictionary *data) {
        XCTAssertNil(data);
        [expectation fulfill];
    }];
    [nativeBridge sendRequest:request withResponseListener:listener];
    
    [self waitForExpectationToFullFillOrTimeOut];
}

-(void)testSendRequestWithEmptyRequestDataAndNEmptyResponseJSToNative
{
    XCTestExpectation* expectation = [self createExpectationWithDescription:@"testSampleRequestNativeToNative"];
    
    id<ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    id<ElectrodeReactBridge> reactBridge = [self getReactBridge];
    
    [nativeBridge regiesterRequestHandlerWithName:@"testRequest" handler:[[TestRequestHandler alloc] initWithOnRequestBlock:^(NSDictionary *data, id<ElectrodeBridgeResponseListener> responseListener) {
        XCTAssertNil(data);
        [responseListener onSuccess:nil];
    }] error:nil];
    
    [self addMockEventListener:[[MockJSEeventListener alloc] initWithResponseBlock:^(ElectrodeBridgeResponse *response) {
        XCTAssertNotNil(response);
        [expectation fulfill];
    }] forName:@"testRequest"];
    
    NSDictionary *jsRequest = [self createBridgeRequestForName:@"testRequest" id:[ElectrodeBridgeMessage UUID] data:nil];
    [reactBridge sendMessage:jsRequest];
    
    [self waitForExpectationToFullFillOrTimeOut];
}



-(void)testSendRequestWithRequestDataAndEmptyResponse
{
    
}

//tests Native request to JS with empty "data" (data = nil) as a response
- (void)testSendRequestWithRequestDataAndEmptyResponseWithJSRequestHandler {
    NSString* const name = @"testingSendRequest";
    ElectrodeBridgeRequestNew* request = [ElectrodeBridgeRequestNew createRequestWithName:name data:nil];
    XCTestExpectation* expectation = [self createExpectationWithDescription:@"sendRequestWithRequestDataAndEmptyResponseWithJSRequestHandler"];
    [self addMockEventListener:[[MockJSEeventListener alloc]  initWithRequestBlock:^(ElectrodeBridgeRequestNew *request) {
        id <ElectrodeReactBridge> reactBridge = [self getReactBridge];
        //do mock JS response here
        NSDictionary* emptyResponse = [self createResponseDataWithName:name id:request.messageId data:nil];
        [reactBridge sendMessage:emptyResponse];
    }] forName:name];
    id <ElectrodeNativeBridge> nativeBridge = [self getNativeBridge];
    MockElectrodeBridgeResponseListener* responseListener = [[MockElectrodeBridgeResponseListener alloc] initWithExpectation:expectation successBlock:^(NSDictionary *data) {
        XCTAssertNil(data);
        [expectation fulfill];
    }];
    [nativeBridge sendRequest:request withResponseListener:responseListener];
    [self waitForExpectationToFullFillOrTimeOut];
}



-(void)testEmitEventWithSimpleDataFromNative
{
    
}

-(void)testEmitEventWithSimpleDataFromJS
{
    
}

-(void)testEmitEventWithComplexDataFromNative
{
    
}

-(void)testEmitEventWithComplexDataFromJS
{
    
}

-(void)testGetEmptyArrayFromJsToNative
{
    
}

-(void)testGetArrayFromJsToNative
{
    
}

-(XCTestExpectation*) createExpectationWithDescription:(nullable NSString*)description
{
    return [self expectationWithDescription:description];
}

-(void) waitForExpectationToFullFillOrTimeOut
{
    [self waitForExpectationsWithTimeout:10.0 handler:^(NSError * _Nullable error) {
        NSLog(@"Test timedout");
    }];
}
@end
