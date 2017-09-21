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
