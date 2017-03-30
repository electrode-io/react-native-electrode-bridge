//
//  ElectrodeBridgeResponseTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/29/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeResponse.h"

@interface ElectrodeBridgeResponseTests : XCTestCase
@end

@implementation ElectrodeBridgeResponseTests

- (void)testResponseData {
    NSString* responseName = @"com.walmart.ern.response";
    NSDictionary* responseData = @{@"test" : @"response"};
    NSString* responseId = [ElectrodeBridgeMessage UUID];
    ElectrodeMessageType responseType = ElectrodeMessageTypeResponse;
    NSDictionary* data = @{
                           kElectrodeBridgeMessageName : responseName,
                           kElectrodeBridgeMessageId : responseId,
                           kElectrodeBridgeMessageType : [ElectrodeBridgeMessage convertEnumTypeToString:responseType] ,
                           kElectrodeBridgeMessageData : responseData
                           };
    ElectrodeBridgeResponse* response = [ElectrodeBridgeResponse createResponseWithData:data];
    XCTAssertNotNil(response, @"No response");
    XCTAssertEqual(response.name, responseName, @"Invalid assignment");
    XCTAssertEqual(response.data, responseData, @"Invalid assignment");
    XCTAssertEqual(response.messageId, responseId, @"Invalid assignment");
    XCTAssertEqual(response.type, responseType, @"Invalid assignment");
    NSString *message = (NSString *) [[data objectForKey:kElectrodeBridgeResponseError] objectForKey:kElectrodeBridgeResponseErrorMessage];
    if (message) {
        XCTFail(@"response has an error message");
    }
}

@end
