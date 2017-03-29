//
//  ElectrodeReactNativeBridgeTests.m
//  ElectrodeReactNativeBridgeTests
//
//  Created by Bharath Marulasiddappa on 1/23/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <React/RCTBridge.h>
#import <React/RCTBridgeModule.h>
#import "ElectrodeBridge.h"
#import "ElectrodeBridgeHolder.h"
#import "ElectrodeBridgeRequest.h"
#import "ElectrodeRequestDispatcher.h"
#import "ElectrodeResponseBlock.h"


@interface ElectrodeReactNativeBridgeTests : XCTestCase

@end

@implementation ElectrodeReactNativeBridgeTests

- (void)testElectrodeBridgeUniqueInstance {
    ElectrodeBridge* uniqueBridgeInstance = [[ElectrodeBridge alloc] init];
    //    XCTAssertNil(uniqueBridgeInstance, @"Electrode bridge initialization error: %@", uniqueBridgeInstance);
    XCTAssertNotNil(uniqueBridgeInstance,@"Electrode Bridge instance is not nill");
    XCTAssert([uniqueBridgeInstance isKindOfClass:[ElectrodeBridge class]]);
}


- (void)testDispatchEvent {
    ElectrodeBridge* bridge = [[ElectrodeBridge alloc] init];
    //RequestCompletionListener* completionListener = [[RequestCompletionListener alloc] init];
    [bridge sendRequest:[[ElectrodeBridgeRequest alloc]  initWithName:@"com.walmartlabs.ern.test" data:nil mode:1] completionListener:[[ElectrodeResponseBlock alloc] initWithSuccess:^(NSDictionary *data) {
        XCTAssertNotNil(data, @"Data is nil");
    } failureBlock:^(NSString *errorCode, NSString *errorMessage) {
        XCTAssert(errorCode, @"%@", [NSError errorWithDomain:@"Bridge couldn't handle a request" code:[errorCode integerValue] userInfo:nil].localizedDescription);
    }]];
    
}

- (void)testDispatchRequest {
    
}

- (void)testEmitEvent {
    
}


- (void)testElectrodeBridgeHolderUniqueInstance {
    ElectrodeBridgeHolder* bridgeHolder = [[ElectrodeBridgeHolder alloc] init];
    //    XCTAssertNil(bridgeHolder, @"Bridge holder instance is nil %@", bridgeHolder);
    XCTAssertNotNil(bridgeHolder,@"Electrode Bridge Holder instance is nil!");
    XCTAssert([bridgeHolder isKindOfClass:[ElectrodeBridgeHolder class]]);
}

- (void)testElectrodeBridgeHolderSingletonInstance {
    ElectrodeBridgeHolder* bridgeHolderSharedInstance = [ElectrodeBridgeHolder sharedInstance];
    XCTAssertNotNil(bridgeHolderSharedInstance, @"Electrode bridge Holder SharedInstance is nil!");
    XCTAssert([bridgeHolderSharedInstance isKindOfClass:[ElectrodeBridgeHolder class]]);
}

- (void)testElectrodeBridgeHolderSingletonAlwaysReturnSameInstance {
    ElectrodeBridgeHolder* bridgeHolderSharedInstanceOne = [ElectrodeBridgeHolder sharedInstance];
    ElectrodeBridgeHolder* bridgeHolderSharedInstanceTwo = [ElectrodeBridgeHolder sharedInstance];
    XCTAssertEqual(bridgeHolderSharedInstanceOne, bridgeHolderSharedInstanceTwo, @"Electrode Bridge holder shared instances are different and not singleton objects");
}

- (void)testElectrodeBridgeHolderSingletonInstanceIsNotNewInstance {
    ElectrodeBridgeHolder* bridgeHolder = [[ElectrodeBridgeHolder alloc] init];
    XCTAssertNotNil(bridgeHolder, @"Electrode Bridge holder instance is nil");
    ElectrodeBridgeHolder* bridgeHolerSharedInstance = [ElectrodeBridgeHolder sharedInstance];
    XCTAssertNotNil(bridgeHolerSharedInstance, @"Electrode Bridge holder shared instance is nil");
    XCTAssertNotEqual(bridgeHolder, bridgeHolerSharedInstance, @"");
}

- (void)testElectrodeBridgeHolderUniqueInstanceReturnsDifferentInstances {
    ElectrodeBridgeHolder *uniqueInstanceOne = [[ElectrodeBridgeHolder alloc] init];
    ElectrodeBridgeHolder *uniqueInstanceTwo = [[ElectrodeBridgeHolder alloc] init];
    XCTAssertNotNil(uniqueInstanceOne);
    XCTAssertNotNil(uniqueInstanceTwo);
    XCTAssertNotEqual(uniqueInstanceOne, uniqueInstanceTwo, @"Instances are same");
}

@end
