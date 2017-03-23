//
//  ElectrodeBridgeTransceiver.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransceiver.h"
#import "ElectrodeEventDispatcher.h"
#import "ElectrodeRequestDispatcher.h"
#import "ElectrodeBridgeTransaction.h"
#import "ElectrodeEventRegistrar.h"
#import "ElectrodeRequestRegistrar.h"



@interface ElectrodeBridgeTransceiver()

@property(nonatomic, copy) NSString *name;
@property(nonatomic, strong) ElectrodeEventDispatcher *eventDispatcher;
@property(nonatomic, strong) ElectrodeRequestDispatcher *requestDispatcher;
@property(nonatomic, copy) NSMutableDictionary<NSString *, ElectrodeBridgeTransaction * > *pendingTransaction;
@property (nonatomic, assign) dispatch_queue_t syncQueue; //this is used to make sure access to pendingTransaction is thread safe.

@end
//CLAIRE TODO: check what are the methods that needs to mark with RCT_EXPORT_METHOD 
@implementation ElectrodeBridgeTransceiver

+(instancetype)sharedInstance {
    static ElectrodeBridgeTransceiver *sharedInstance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        sharedInstance = [[ElectrodeBridgeTransceiver alloc] init];
    });
    return sharedInstance;
}

-(instancetype)init {
    if (self = [super init])
    {
        self.eventDispatcher = [[ElectrodeEventDispatcher alloc] init];
        self.requestDispatcher = [[ElectrodeRequestDispatcher alloc] init];
        self.syncQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        self.pendingTransaction = [[NSMutableDictionary alloc] init];
    }
    return self;
}

+ (NSArray *)electrodeModules
{
    return @[[[ElectrodeBridgeTransceiver alloc] init]];
}

-(NSString *) name {
    return @"ElectrodeNativeBridge";
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma ElectrodeNativeBridge implementation
-(void)sendRequest:(ElectrodeBridgeRequestNew *)request withResponseListener:(id<ElectrodeBridgeEventListener>) responseListener {
    
}

-(void)regiesterRequestHandlerWithName: (NSString *)name handler:(id<ElectrodeBridgeRequestHandler>) requestHandler {
    
}


-(void)sendEvent: (ElectrodeBridgeEvent *)event {
    
}

-(NSUUID *)addEventListenerWithName: (NSString *)name eventListener: (id<ElectrodeBridgeEventListener>) eventListener {
    NSLog(@"%@, Adding eventListener %@ for event %@", NSStringFromClass([self class]), eventListener, name);
    return [NSUUID UUID]; //CLAIRE TODO: in the progress of fixing it 
}

@end
