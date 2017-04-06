//
//  ElectrodeBridgeHolderNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/28/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransceiver.h"
#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeHolderNew.h"


@interface ElectrodeBridgeHolderNew()

@property(nonatomic, assign) BOOL isReactNativeReady;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeRequestHandler>> *queuedRequestHandlerRegistration;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeEventListener>> *queuedEventListenerRegistration;
@property(nonatomic, copy) NSMutableDictionary<ElectrodeBridgeRequestNew *,id<ElectrodeBridgeResponseListener>> *queuedRequests;
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
    isReactNativeReady = YES;
    electrodeNativeBridge = [ElectrodeBridgeTransceiver sharedInstance];
    queuedRequestHandlerRegistration = [[NSMutableDictionary alloc] init];
    queuedEventListenerRegistration = [[NSMutableDictionary alloc] init];
    queuedRequests = [[NSMutableDictionary alloc] init];
    queuedEvent = [[NSMutableArray alloc] init];
    
    [ElectrodeBridgeHolderNew registerReactReadyListenr];
}

+ (void)registerReactReadyListenr {
    [ElectrodeBridgeTransceiver registerReactNativeReadyListener:^{
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
   responseListener:(id<ElectrodeBridgeResponseListener> _Nonnull)responseListener
{
    if (!isReactNativeReady) {
        [queuedRequests setObject:responseListener forKey:request];
    } else {
        [electrodeNativeBridge sendRequest:request withResponseListener:responseListener];
    }
}

+ (void)registerRequestHanlderWithName: (NSString *)name
                        requestHandler: (id<ElectrodeBridgeRequestHandler> _Nonnull) requestHandler
{
    if(!isReactNativeReady) {
        [queuedRequestHandlerRegistration setObject:requestHandler forKey:name];
    } else {
        NSError *error;
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
    for (NSString *requestName in queuedRequestHandlerRegistration) {
        id<ElectrodeBridgeRequestHandler> requestHandler = queuedRequestHandlerRegistration[requestName];
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
        id<ElectrodeBridgeResponseListener> responseListener = queuedRequests[request];
        [ElectrodeBridgeHolderNew sendRequest:request responseListener:responseListener];
    }
    
    [queuedRequests removeAllObjects];
}

+ (void) sendQueuedEvents {
    for (ElectrodeBridgeEventNew *event in queuedEvent) {
        [ElectrodeBridgeHolderNew sendEvent:event];
    }
    
    [queuedEvent removeAllObjects];
}
@end
