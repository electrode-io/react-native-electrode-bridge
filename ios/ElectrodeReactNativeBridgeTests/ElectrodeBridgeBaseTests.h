//
//  ElectrodeBridgeBaseTests.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/27/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "ElectrodeBridgeRequestNew.h"
#import <Foundation/Foundation.h>
#import "ElectrodeBridgeMessage.h"
#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeTransceiver.h"
#import <React/RCTBridgeDelegate.h>
#import <React/RCTBridge.h>




extern const int kElectrodeBridgeRequestTestTimeOut;


@interface ElectrodeBridgeBaseTests : XCTestCase<RCTBridgeDelegate>
-(void)initializeBundle;
@property(nonatomic, strong, readonly) RCTBridge *bridge;
@end

@interface ElectrodeBridgeRequestNew (CustomeBuilder)

+(instancetype)createRequestWithName: (NSString *)name;

@end

@interface MockElectrodeBridgeResponseListener : XCTestCase<ElectrodeBridgeResponseListener>
@property(nonatomic, assign) BOOL isSuccessListener;
@property(nonatomic, assign) int testTimeOutMs;
@property(nonatomic, strong) XCTestExpectation *expectation;
@end




