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
#import "ElectrodeBridgeRequestNew.h"
#import "ElectrodeBridgeProtocols.h"


@interface MockElectrodeBridgeResponseListener : NSObject <ElectrodeBridgeResponseListener>
@end

@interface ElectrodeBridgeTransactionTests : XCTestCase
@end

@implementation ElectrodeBridgeTransactionTests
- (void)testBridgeTransaction {
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
    ElectrodeBridgeRequestNew* requestMessage = [ElectrodeBridgeRequestNew createRequestWithData:data];
    XCTAssertNotNil(requestMessage, @"request is not created");
    MockElectrodeBridgeResponseListener* responseListener = [[MockElectrodeBridgeResponseListener alloc] init];
    ElectrodeBridgeTransaction* transaction = [[ElectrodeBridgeTransaction alloc] initWithRequest:requestMessage responseListener:responseListener];
    XCTAssertNotNil(transaction, @"transaction is not created");
    XCTAssertEqual(transaction.finalResponseListener, responseListener, @"invalid assignment");
    XCTAssertEqual(transaction.request, requestMessage, @"invalid assignment");
    XCTAssertEqual(transaction.transactionId, requestMessage.messageId, @"invalid assignment");
}

@end
