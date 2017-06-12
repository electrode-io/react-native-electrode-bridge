//
//  ElectrodeBridgeEventTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/29/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeEvent.h"

@interface ElectrodeBridgeEventTests : XCTestCase

@end

@implementation ElectrodeBridgeEventTests

- (void)testEventInstance {
    NSString* eventName = @"com.walmart.ern.event";
    NSDictionary* eventData = @{@"test" : @"testevent"};
    NSString* eventId = [ElectrodeBridgeMessage UUID];
    ElectrodeMessageType eventType = ElectrodeMessageTypeEvent;
    NSDictionary* data = @{
                           kElectrodeBridgeMessageName : eventName,
                           kElectrodeBridgeMessageId : eventId,
                           kElectrodeBridgeMessageType : [ElectrodeBridgeMessage convertEnumTypeToString:eventType] ,
                           kElectrodeBridgeMessageData : eventData
                           };
    ElectrodeBridgeEvent* event = [ElectrodeBridgeEvent createEventWithData:data];
    
    XCTAssertNotNil(event, @"event object is not created");
    XCTAssertEqual(event.name, eventName, @"Invalid assignment");
    XCTAssertEqual(event.data, eventData, @"Invalid assignment");
    XCTAssertEqual(event.messageId, eventId, @"Invalid assignment");
    XCTAssertEqual(event.type, eventType, @"Invalid assignment");
}

@end
