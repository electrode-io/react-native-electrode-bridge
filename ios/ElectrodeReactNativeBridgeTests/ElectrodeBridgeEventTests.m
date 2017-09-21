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
