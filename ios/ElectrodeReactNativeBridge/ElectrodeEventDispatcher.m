//
//  ElectrodeEventDispatcher.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/24/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeEventDispatcher.h"
#import "ElectrodeBridgeProtocols.h"

@interface ElectrodeEventRegistrar()

@property(nonatomic, strong) ElectrodeEventRegistrar *eventRegistrar;

@end

@implementation ElectrodeEventDispatcher

-(instancetype)initWithEventRegistrar: (ElectrodeEventRegistrar *)eventRegistrar
{
    if (self = [super init]) {
        _eventRegistrar = eventRegistrar;
    }
    
    return self;
}

-(void)dispatchEvent: (ElectrodeBridgeEvent *)bridgeEvent
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
