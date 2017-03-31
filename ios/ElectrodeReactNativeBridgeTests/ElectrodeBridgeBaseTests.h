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
#import "ElectrodeBridgeResponse.h"
#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeTransceiver.h"
#import <React/RCTBridgeDelegate.h>
#import <React/RCTBridge.h>

extern const int kElectrodeBridgeRequestTestTimeOut;

/////////////////MockJSEeventListener

typedef void (^evetBlock)(ElectrodeBridgeEventNew *request);
typedef void (^requestBlock)(ElectrodeBridgeRequestNew *request);
typedef void (^responseBlock)(ElectrodeBridgeResponse *response);

@interface MockJSEeventListener : NSObject
-(nonnull instancetype) initWithEventBlock:(nonnull evetBlock) evetBlock;
-(nonnull instancetype) initWithRequestBlock:(nonnull requestBlock) requestBlock;
-(nonnull instancetype) initWithResponseBlock:(nonnull responseBlock) responseBlock;

@property(nonatomic, copy, nullable) evetBlock evetBlock;
@property(nonatomic, copy, nullable) requestBlock requestBlock;
@property(nonatomic, copy, nullable) responseBlock responseBlock;
@end


/////////////////ElectrodeBridgeBaseTests

@interface ElectrodeBridgeBaseTests : XCTestCase<RCTBridgeDelegate>
-(void)initializeBundle;
@property(nonatomic, strong, readonly) RCTBridge *bridge;
@property(nonatomic, strong, nonnull) NSMutableDictionary<NSString *, MockJSEeventListener *> *mockListenerStore;

-(void) addMockEventListener:(MockJSEeventListener *) mockJsEventListener forName:(NSString *)name;
-(id<ElectrodeNativeBridge>) getNativeBridge;
-(id<ElectrodeReactBridge>) getReactBridge;

@end



/////////////////ElectrodeBridgeRequestNew
@interface ElectrodeBridgeRequestNew (CustomeBuilder)

+(instancetype)createRequestWithName: (NSString *)name;

@end

/////////////////MockElectrodeBridgeResponseListener
typedef void (^successBlock)(NSDictionary *data);
typedef void (^failureBlock)(id<ElectrodeFailureMessage> failureMessage);

@interface MockElectrodeBridgeResponseListener : XCTestCase<ElectrodeBridgeResponseListener>
- (nonnull instancetype) initWithExpectation: (nonnull XCTestExpectation *) expectation successBlock:(nonnull successBlock) success NS_DESIGNATED_INITIALIZER;
- (nonnull instancetype) initWithExpectation: (nonnull XCTestExpectation *) expectation failureBlock:(nonnull failureBlock) failure NS_DESIGNATED_INITIALIZER;

@property(nonatomic, assign) BOOL isSuccessListener;
@property(nonatomic, assign) int testTimeOutMs;
@property(nonatomic, strong, nonnull) XCTestExpectation* expectation;

@property(nonatomic, copy, nullable) successBlock successBlk;
@property(nonatomic, copy, nullable) failureBlock failureBlk;
@end

/////////////////MockBridgeTransceiver
@interface MockBridgeTransceiver : ElectrodeBridgeTransceiver
- (nonnull instancetype) initWithJsMockListenerStore:(nonnull NSMutableDictionary<NSString *, MockJSEeventListener *> *)mockListenerStore NS_DESIGNATED_INITIALIZER;

@property(nonatomic, strong, nonnull) NSMutableDictionary<NSString *, MockJSEeventListener *> *mockListenerStore;
@end



