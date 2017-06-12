//
//  ElectrodeBridgeRequestTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/29/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeRequest.h"

@interface ElectrodeBridgeRequestTests : XCTestCase

@end

@implementation ElectrodeBridgeRequestTests

- (void)testRequestInstance {
    NSString* messageName = @"com.walmart.ern.requestmessage";
    NSDictionary* messageData = @{@"test" : @"requestMessage"};
    NSString* messageId = [ElectrodeBridgeMessage UUID];
    ElectrodeMessageType messageType = ElectrodeMessageTypeRequest;
    NSDictionary* data = @{
                           kElectrodeBridgeMessageName : messageName,
                           kElectrodeBridgeMessageId : messageId,
                           kElectrodeBridgeMessageType : [ElectrodeBridgeMessage convertEnumTypeToString:messageType] ,
                           kElectrodeBridgeMessageData : messageData
                           };
    ElectrodeBridgeRequest* requestMessage = [ElectrodeBridgeRequest createRequestWithData:data];
    XCTAssertNotNil(requestMessage, @"requestMessage is not created");
    XCTAssertEqual(requestMessage.name, messageName, @"Invalid assignment");
    XCTAssertEqual(requestMessage.data, messageData, @"Invalid assignment");
    XCTAssertEqual(requestMessage.messageId, messageId, @"Invalid assignment");
    XCTAssertEqual(requestMessage.type, messageType, @"Invalid assignment");
}                


@end
