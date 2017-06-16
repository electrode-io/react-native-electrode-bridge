//
//  ElectrodeBridgeTransactionTests.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/30/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

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
    }];
    XCTAssertNotNil(transaction, @"transaction instance error");
    XCTAssertNotNil(transaction.completion, @"transaction's completion handler is nil");
    XCTAssertEqual(transaction.request, request, @"invalid assignment");
    XCTAssertEqual(transaction.transactionId, request.messageId, @"invalid assignment");
}

@end
