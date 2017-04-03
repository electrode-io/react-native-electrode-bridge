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

-(void)testSendRequestWithRequestDataAndEmptyResponseWithJSRequestHandler
{
    
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
    [self waitForExpectationsWithTimeout:5.0 handler:^(NSError * _Nullable error) {
        NSLog(@"Test timedout");
    }];
}
@end
