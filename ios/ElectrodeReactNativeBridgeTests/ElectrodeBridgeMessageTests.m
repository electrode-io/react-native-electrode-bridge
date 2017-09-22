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

-(void)testPrimitiveTypeAsData {
    NSString* messageName = @"com.walmart.ern.requestmessage";
    NSDictionary* messageData = @{@"test" : @"requestMessage"};
    NSString* messageId = [ElectrodeBridgeMessage UUID];
    ElectrodeMessageType messageType = ElectrodeMessageTypeRequest;
    NSDictionary* data = @{
                           kElectrodeBridgeMessageName : messageName,
                           kElectrodeBridgeMessageId : messageId,
                           kElectrodeBridgeMessageType : [ElectrodeBridgeMessage convertEnumTypeToString:messageType] ,
                           kElectrodeBridgeMessageData : @3
                           };
    
    ElectrodeBridgeMessage* requestMessage = [[ElectrodeBridgeMessage alloc] initWithData:data];
    XCTAssertEqual(requestMessage.data, @3);
}

@end
