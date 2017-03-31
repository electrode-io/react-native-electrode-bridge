//
//  ElectrodeBridgeHolderNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/28/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeHolderNew.h"
#import "ElectrodeBridgeTransceiver.h"
#import "ElectrodeBridgeProtocols.h"


@interface ElectrodeBridgeHolderNew()

@property(nonatomic, assign) BOOL isReactNativeReady;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeRequestHandler>> *queuedRequestHandlerRegistration;
@property(nonatomic, copy) NSMutableDictionary<NSString *, id<ElectrodeBridgeEventListener>> *queuedEventListenerRegistration;
@property(nonatomic, copy) NSMutableDictionary<ElectrodeBridgeRequestNew *,id<ElectrodeBridgeResponseListener>> *queuedRequests;
@property(nonatomic, copy) NSMutableArray<ElectrodeBridgeEventNew *> *queuedEvents;
@property(nonatomic, strong)ElectrodeBridgeTransceiver *electrodeNativeBridge;
@end

@implementation ElectrodeBridgeHolderNew

+ (instancetype)sharedInstance
{
    static ElectrodeBridgeHolderNew* sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[ElectrodeBridgeHolderNew alloc] init];
    });
    
    return sharedInstance;
}

- (instancetype) init{
    if(self = [super init])
    {
        _queuedRequestHandlerRegistration = [[NSMutableDictionary alloc] init];
        _queuedEventListenerRegistration = [[NSMutableDictionary alloc] init];
        _queuedRequests = [[NSMutableDictionary alloc] init];
        _queuedEvents = [[NSMutableArray alloc] init];
        _electrodeNativeBridge = [[ElectrodeBridgeTransceiver alloc] init];
    }
    
    return self;
}

- (BOOL)isReactNativeReady
{
    return self.electrodeNativeBridge.isReactNativeBridgeReady;
}

- (void)registerReactNativeReadyListener: (ElectrodeBridgeReactNativeReadyListner) listener
{
    [self.electrodeNativeBridge registerReactNativeReadyListener:listener];
}

- (void)sendEvent: (ElectrodeBridgeEventNew *)event
{
    [self.electrodeNativeBridge sendEvent:event];
}

- (void)sendRequest: (ElectrodeBridgeRequestNew *)request
   responseListener:(id<ElectrodeBridgeResponseListener> _Nonnull)responseListener
{
    [self.electrodeNativeBridge sendRequest:request withResponseListener:responseListener];
}

- (void)registerRequestHanlderWithName: (NSString *)name
                        requestHandler: (id<ElectrodeBridgeRequestHandler> _Nonnull) requestHandler
{
    NSError *error;
    [self.electrodeNativeBridge regiesterRequestHandlerWithName:name handler:requestHandler error:&error];
    
    if(error) {
        [NSException raise:@"registration failed" format:@"registration for request handler failed"];
    }
}

- (void)addEventListnerWithName: (NSString *)name
                   eventListner: (id<ElectrodeBridgeEventListener>) eventListner
{
    [self.electrodeNativeBridge addEventListenerWithName:name eventListener:eventListner];
}
@end
