//
//  ElectrodeBridgeMessageTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/27/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeMessage.h"


@interface ElectrodeBridgeMessageTests : XCTestCase

@end

@implementation ElectrodeBridgeMessageTests

- (void)testRequestMessageInstanceUsingDictionary {
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
    ElectrodeBridgeMessage* requestMessage = [[ElectrodeBridgeMessage alloc] initWithData:data];
    XCTAssertNotNil(requestMessage, @"requestMessageInstance is not created");
    XCTAssertEqual(requestMessage.name, messageName, @"Message names are different. Invalid assignment");
    XCTAssertEqual(requestMessage.data, messageData, @"Message data may be different. Invalid assignment");
    XCTAssertEqual(requestMessage.messageId, messageId, @"Message id may be different. Invalid assignment");
    XCTAssertEqual(requestMessage.type, messageType, @"Message type may be different. Invalid assignment");
}

@end
