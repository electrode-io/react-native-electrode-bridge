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


typedef void (^successBlock)(NSDictionary *data);
typedef void (^failureBlock)(id<ElectrodeFailureMessage> failureMessage);

@interface MockElectrodeBridgeResponseListener : XCTestCase<ElectrodeBridgeResponseListener>
- (instancetype) initWithExpectation: (nonnull XCTestExpectation *) expectation successBlock:(nonnull successBlock) success NS_DESIGNATED_INITIALIZER;
- (instancetype) initWithExpectation: (nonnull XCTestExpectation *) expectation failureBlock:(nonnull failureBlock) failure NS_DESIGNATED_INITIALIZER;

@property(nonatomic, assign) BOOL isSuccessListener;
@property(nonatomic, assign) int testTimeOutMs;
@property(nonatomic, strong, nonnull) XCTestExpectation* expectation;

@property(nonatomic, copy, nullable) successBlock successBlk;
@property(nonatomic, copy, nullable) failureBlock failureBlk;
@end




