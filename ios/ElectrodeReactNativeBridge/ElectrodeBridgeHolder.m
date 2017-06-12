//
//  ElectrodeBridgeHolder.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/28/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeProtocols.h"
#import "ElectrodeBridgeHolder.h"
#import "ElectrodeBridgeTransceiver.h"

NS_ASSUME_NONNULL_BEGIN
@interface ElectrodeBridgeHolder()

@property(nonatomic, assign) BOOL isReactNativeReady;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeRequestHandler>> *queuedRequestHandlerRegistration;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeEventListener>> *queuedEventListenerRegistration;
@property(nonatomic, copy) NSMutableDictionary<ElectrodeBridgeRequest *,NSArray *> *queuedRequests;
@property(nonatomic, copy) NSMutableArray<ElectrodeBridgeEvent *> *queuedEvents;
@end

@implementation ElectrodeBridgeHolder
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
    
    [ElectrodeBridgeHolder registerReactReadyListenr];
}

+ (void)registerReactReadyListenr {
    [ElectrodeBridgeTransceiver registerReactNativeReadyListener:^(ElectrodeBridgeTransceiver * _Nonnull transceiver) {
        isReactNativeReady = YES;
        electrodeNativeBridge = transceiver;
        [ElectrodeBridgeHolder registerQueuedEventListeners];
        [ElectrodeBridgeHolder registerQueuedRequestHandlers];
        [ElectrodeBridgeHolder sendQueuedEvents];
        [ElectrodeBridgeHolder sendQueuedRequests];
    }];
}

+ (instancetype)sharedInstance
{
    static ElectrodeBridgeHolder* sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[ElectrodeBridgeHolder alloc] init];
    });
    
    return sharedInstance;
}

+ (void)sendEvent: (ElectrodeBridgeEvent *)event
{
    if (!isReactNativeReady) {
        [queuedEvent addObject:event];
    } else {
        [electrodeNativeBridge sendEvent:event];
    }
}

+ (void)sendRequest: (ElectrodeBridgeRequest *)request
  completionHandler: (ElectrodeBridgeResponseCompletionHandler) completion
{
    if (!isReactNativeReady) {
        [queuedRequests setObject: completion forKey:request];
    } else {
        [electrodeNativeBridge sendRequest:request completionHandler: completion];
    }
}

+ (void)registerRequestHanlderWithName: (NSString *)name
                        requestCompletionHandler: (ElectrodeBridgeRequestCompletionHandler) completion
{
    if(!isReactNativeReady) {
        [queuedRequestHandlerRegistration setObject:completion forKey:name];
        NSLog(@"queuedRequestHandlerRegistration when react is not ready %@", queuedRequestHandlerRegistration);
    } else {
        NSError *error;
        NSLog(@"BridgeHolderNew: registering request handler with name %@",name);
        [electrodeNativeBridge registerRequestCompletionHandlerWithName:name completionHandler:completion];
        
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
        ElectrodeBridgeRequestCompletionHandler requestHandler = queuedRequestHandlerRegistration[requestName];
        NSLog(@"requestName name for handler");
        [ElectrodeBridgeHolder registerRequestHanlderWithName:requestName requestCompletionHandler:requestHandler];
    }
    
    [queuedRequestHandlerRegistration removeAllObjects];
}

+ (void) registerQueuedEventListeners {
    for (NSString *eventListnerName in queuedEventListenerRegistration) {
        id<ElectrodeBridgeEventListener> eventListener = queuedEventListenerRegistration[eventListnerName];
        [ElectrodeBridgeHolder addEventListnerWithName:eventListnerName eventListner:eventListener];
    }
    
    [queuedEventListenerRegistration removeAllObjects];
}

+ (void) sendQueuedRequests {
    for (ElectrodeBridgeRequest *request in queuedRequests) {
        ElectrodeBridgeResponseCompletionHandler completion = queuedRequests[request];
        [ElectrodeBridgeHolder sendRequest:request completionHandler: completion];
    }
    
    [queuedRequests removeAllObjects];
}

+ (void) sendQueuedEvents {
    for (ElectrodeBridgeEvent *event in queuedEvent) {
        [ElectrodeBridgeHolder sendEvent:event];
    }
    
    [queuedEvent removeAllObjects];
}

+ (void) setBridge: (ElectrodeBridgeTransceiver *)bridge{
    isReactNativeReady = YES;
    electrodeNativeBridge = bridge;
}

@end
NS_ASSUME_NONNULL_END
