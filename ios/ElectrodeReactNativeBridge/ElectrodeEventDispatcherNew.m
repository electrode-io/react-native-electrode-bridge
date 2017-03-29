//
//  ElectrodeEventDispatcherNew.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeEventDispatcherNew.h"
#import "ElectrodeBridgeProtocols.h"

@interface ElectrodeEventRegistrarNew()

@property(nonatomic, strong) ElectrodeEventRegistrarNew *eventRegistrar;

@end

@implementation ElectrodeEventDispatcherNew

-(instancetype)initWithEventRegistrar: (ElectrodeEventRegistrarNew *)eventRegistrar
{
    if (self = [super init]) {
        _eventRegistrar = eventRegistrar;
    }
    
    return self;
}

-(void)dispatchEvent: (ElectrodeBridgeEventNew *)bridgeEvent
{
    NSArray<ElectrodeBridgeEventListener> *eventListeners = [self.eventRegistrar getEventListnersForName:bridgeEvent.name];
    
    for (id<ElectrodeBridgeEventListener> eventListener in eventListeners) {
        NSLog(@"ElectrodeEventDispatcher is dispatching events %@, id(%@) to listener %@", bridgeEvent.name, bridgeEvent.messageId, eventListener);
        dispatch_async(dispatch_get_main_queue(), ^{
            [eventListener onEvent:bridgeEvent.data];
        });
    }
}

@end
