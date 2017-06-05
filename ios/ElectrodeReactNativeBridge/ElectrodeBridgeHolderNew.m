//
//  ElectrodeBridgeHolderNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/28/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeHolderNew.h"
#import "ElectrodeBridgeTransceiver.h"


@interface ElectrodeBridgeHolderNew()

@property(nonatomic, assign) BOOL isReactNativeReady;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeRequestHandler>> *queuedRequestHandlerRegistration;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeEventListener>> *queuedEventListenerRegistration;
@property(nonatomic, copy) NSMutableDictionary<ElectrodeBridgeRequestNew *,NSArray *> *queuedRequests;
@property(nonatomic, copy) NSMutableArray<ElectrodeBridgeEventNew *> *queuedEvents;
@end

@implementation ElectrodeBridgeHolderNew
static ElectrodeBridgeTransceiver *electrodeNativeBridge;
static BOOL isReactNativeReady = NO;
static NSMutableDictionary *queuedRequestHandlerRegistration;
static NSMutableDictionary *queuedEventListenerRegistration;
static NSMutableDictionary *queuedRequests;
static NSMutableArray *queuedEvent;

+(void) initialize {
    NSLog(@"in bridge holder initialization");
    queuedRequestHandlerRegistration = [[NSMutableDictionary alloc] init];
    queuedEventListenerRegistration = [[NSMutableDictionary alloc] init];
    queuedRequests = [[NSMutableDictionary alloc] init];
    queuedEvent = [[NSMutableArray alloc] init];
    
    [ElectrodeBridgeHolderNew registerReactReadyListenr];
}

+ (void)registerReactReadyListenr {
    [ElectrodeBridgeTransceiver registerReactNativeReadyListener:^(ElectrodeBridgeTransceiver * _Nonnull transceiver) {
        isReactNativeReady = YES;
        electrodeNativeBridge = transceiver;
        [ElectrodeBridgeHolderNew registerQueuedEventListeners];
        [ElectrodeBridgeHolderNew registerQueuedRequestHandlers];
        [ElectrodeBridgeHolderNew sendQueuedEvents];
        [ElectrodeBridgeHolderNew sendQueuedRequests];
    }];
}

+ (instancetype)sharedInstance
{
    static ElectrodeBridgeHolderNew* sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[ElectrodeBridgeHolderNew alloc] init];
    });
    
    return sharedInstance;
}

+ (void)sendEvent: (ElectrodeBridgeEventNew *)event
{
    if (!isReactNativeReady) {
        [queuedEvent addObject:event];
    } else {
        [electrodeNativeBridge sendEvent:event];
    }
}

+ (void)sendRequest: (ElectrodeBridgeRequestNew *)request
            success: (ElectrodeBridgeResponseListenerSuccessBlock _Nonnull) success
            failure: (ElectrodeBridgeResponseListenerFailureBlock _Nonnull) failure
{
    if (!isReactNativeReady) {
        [queuedRequests setObject:@[[success copy], [failure copy]] forKey:request];
    } else {
        [electrodeNativeBridge sendRequest:request success:success failure:failure];
    }
}

+ (void)registerRequestHanlderWithName: (NSString *)name
                        requestHandler: (id<ElectrodeBridgeRequestHandler> _Nonnull) requestHandler
{
    if(!isReactNativeReady) {
        [queuedRequestHandlerRegistration setObject:requestHandler forKey:name];
        NSLog(@"queuedRequestHandlerRegistration when react is not ready %@", queuedRequestHandlerRegistration);
    } else {
        NSError *error;
        NSLog(@"BridgeHolderNew: registering request handler with name %@",name);
        [electrodeNativeBridge regiesterRequestHandlerWithName:name handler:requestHandler error:&error];
        
        if(error) {
            [NSException raise:@"registration failed" format:@"registration for request handler failed"];
        }
    }
}

+ (void)addEventListnerWithName: (NSString *)name
                   eventListner: (id<ElectrodeBridgeEventListener>) eventListner
{
    if (!isReactNativeReady) {
        [queuedEventListenerRegistration setObject:eventListner forKey:name];
    } else {
        [electrodeNativeBridge addEventListenerWithName:name eventListener:eventListner];
    }
}


+ (BOOL) isReactNativeReady {
    return isReactNativeReady;
}

+ (void) registerQueuedRequestHandlers {
    NSLog(@"registering Queued requesters");
    NSLog(@"queuedRequestHandlerRegistration %@", queuedRequestHandlerRegistration);
    for (NSString *requestName in queuedRequestHandlerRegistration) {
        id<ElectrodeBridgeRequestHandler> requestHandler = queuedRequestHandlerRegistration[requestName];
        NSLog(@"requestName name for handler");
        [ElectrodeBridgeHolderNew registerRequestHanlderWithName:requestName requestHandler:requestHandler];
    }
    
    [queuedRequestHandlerRegistration removeAllObjects];
}

+ (void) registerQueuedEventListeners {
    for (NSString *eventListnerName in queuedEventListenerRegistration) {
        id<ElectrodeBridgeEventListener> eventListener = queuedEventListenerRegistration[eventListnerName];
        [ElectrodeBridgeHolderNew addEventListnerWithName:eventListnerName eventListner:eventListener];
    }
    
    [queuedEventListenerRegistration removeAllObjects];
}

+ (void) sendQueuedRequests {
    for (ElectrodeBridgeRequestNew *request in queuedRequests) {
        NSArray *responseBlock = queuedRequests[request];
        ElectrodeBridgeResponseListenerSuccessBlock successBlock = [responseBlock firstObject];
        ElectrodeBridgeResponseListenerFailureBlock failureBlock = [responseBlock lastObject];
        [ElectrodeBridgeHolderNew sendRequest:request success:successBlock failure:failureBlock];
    }
    
    [queuedRequests removeAllObjects];
}

+ (void) sendQueuedEvents {
    for (ElectrodeBridgeEventNew *event in queuedEvent) {
        [ElectrodeBridgeHolderNew sendEvent:event];
    }
    
    [queuedEvent removeAllObjects];
}

+ (void) setBridge: (ElectrodeBridgeTransceiver *)bridge{
    isReactNativeReady = YES;
    electrodeNativeBridge = bridge;
}

@end
