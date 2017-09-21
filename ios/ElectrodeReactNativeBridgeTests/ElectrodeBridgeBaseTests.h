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
#import "ElectrodeBridgeRequest.h"
#import <Foundation/Foundation.h>
#import "ElectrodeBridgeMessage.h"
#import "ElectrodeBridgeResponse.h"
#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeTransceiver_Internal.h"
#import <React/RCTBridgeDelegate.h>
#import <React/RCTBridge.h>

extern const int kElectrodeBridgeRequestTestTimeOut;
extern  NSString* _Nonnull  const ktestId;

/////////////////MockJSEeventListener
NS_ASSUME_NONNULL_BEGIN
typedef void (^evetBlock)(ElectrodeBridgeEvent *request);
typedef void (^requestBlock)(ElectrodeBridgeRequest *request);
typedef void (^responseBlock)(ElectrodeBridgeResponse *response);
typedef void (^ElectrodeBaseJSBlock)(NSDictionary *result);
@interface MockJSEeventListener : NSObject
-(nonnull instancetype) initWithEventBlock:(nonnull evetBlock) evetBlock;
-(nonnull instancetype) initWithRequestBlock:(nonnull requestBlock) requestBlock;
-(nonnull instancetype) initWithResponseBlock:(nonnull responseBlock) responseBlock;
///////////////
-(nonnull instancetype) initWithRequestBlock: (nonnull requestBlock) requestBlock response: (NSDictionary *)response;
-(nonnull instancetype) initWithjSBlock: (ElectrodeBaseJSBlock) jSBlock;
-(instancetype) initWithjSBlock:(ElectrodeBaseJSBlock)jSBlock response: (id _Nullable) response;
///////////////

@property(nonatomic, copy, nullable) evetBlock evetBlock;
@property(nonatomic, copy, nullable) requestBlock requestBlock;
@property(nonatomic, copy, nullable) responseBlock responseBlock;

@property(nonatomic, copy, nullable) id response;
@property(nonatomic, copy, nullable) ElectrodeBaseJSBlock jSCallBackBlock;
@end

/////////////////ElectrodeBridgeBaseTests
@interface ElectrodeBridgeBaseTests : XCTestCase<RCTBridgeDelegate>
-(void)initializeBundle;
@property(nonatomic, strong, readonly, nonnull) RCTBridge *bridge;
@property(nonatomic, strong, nonnull) NSMutableDictionary<NSString *, MockJSEeventListener *> *mockListenerStore;
/////
-(void) addMockEventListener:(MockJSEeventListener *)mockJsEventListener
                     forName:(NSString *)name; //use this method for bridge test only
-(void) appendMockEventListener:(MockJSEeventListener *)mockJsEventListener
                        forName:(NSString *)name;//use this method for test involves processor
-(void)removeMockEventListenerWithName: (NSString *)name;
-(id<ElectrodeNativeBridge> _Nonnull) getNativeBridge;
-(id<ElectrodeReactBridge> _Nonnull) getReactBridge;
-(nonnull NSDictionary*) createBridgeRequestForName:(nonnull NSString*)name id:(nonnull NSString*)requestId data:(nullable id)data;
- (nonnull NSDictionary *)createResponseDataWithName:(nonnull NSString *)name id:(nonnull NSString *)responseId data:(nullable id)data;
- (nonnull NSDictionary *)createEventDataWithName:(nonnull NSString *)eventName id:(nonnull NSString *)eventId data:(nullable id)eventData;
@end


/////////////////ElectrodeBridgeRequest
@interface ElectrodeBridgeRequest (CustomeBuilder)
+(nonnull instancetype)createRequestWithName: (NSString *)name;
+(nonnull instancetype)createRequestWithName: (NSString *)name data:(nullable id)data;
@end

@interface ElectrodeBridgeEvent (ElectrodeBridgeEventAddition)
+ (nonnull instancetype)createEventWithName:(nonnull NSString *)name data:(nullable id)eventData;
@end

/////////////////MockElectrodeBridgeResponseListener
typedef void (^successBlock) (_Nullable id data);
typedef void (^failureBlock) (_Nullable id<ElectrodeFailureMessage> failureMessage);

/////////////////MockBridgeTransceiver
@interface MockBridgeTransceiver : ElectrodeBridgeTransceiver
@property(nonatomic, strong, nonnull) NSMutableDictionary<NSString *, MockJSEeventListener *> *myMockListenerStore;
@end
NS_ASSUME_NONNULL_END


