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
#import "ElectrodeBridgeTransaction.h"
#import "ElectrodeBridgeRequest.h"
#import "ElectrodeBridgeProtocols.h"


@interface ElectrodeBridgeTransactionTests : XCTestCase
@end

@implementation ElectrodeBridgeTransactionTests

- (void)testTransactionInstance {
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
    ElectrodeBridgeRequest* request = [ElectrodeBridgeRequest createRequestWithData:data];
    XCTAssertNotNil(request, @"request instance error");
    ElectrodeBridgeTransaction* transaction = [[ElectrodeBridgeTransaction alloc] initWithRequest:request completionHandler:^(id  _Nullable data, id<ElectrodeFailureMessage>  _Nullable message) {
        XCTAssert([data isEqualToString:@"blah"]);
    }];
    XCTAssertNotNil(transaction, @"transaction instance error");
    XCTAssertNotNil(transaction.completion, @"transaction's completion handler is nil");
    XCTAssertEqual(transaction.request, request, @"invalid assignment");
    XCTAssertEqual(transaction.transactionId, request.messageId, @"invalid assignment");
    transaction.completion(@"blah", nil);
}

@end
