//
//  ElectrodeEventRegistrar.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeEventRegistrar.h"

@interface ElectrodeEventRegistrar ()

@property(nonatomic, strong) NSMutableDictionary *eventListenerByUUID;
@property(nonatomic, strong) NSMutableDictionary *eventListenersByEventName;

@end

@implementation ElectrodeEventRegistrar
- (NSUUID *_Nonnull)
registerEventListener:(NSString *_Nonnull)name
        eventListener:(ElectrodeBridgeEventListener _Nonnull)eventListener {
  @synchronized(self) {
    if ([self.eventListenersByEventName objectForKey:name]) {
      NSMutableArray *eventListenerArray =
          [self.eventListenersByEventName objectForKey:name];
      [eventListenerArray addObject:eventListener];
      [self.eventListenersByEventName setValue:eventListenerArray forKey:name];
    } else {
      NSMutableArray *eventListenerArray = [[NSMutableArray alloc] init];
      [eventListenerArray addObject:eventListener];
      [self.eventListenersByEventName setObject:eventListenerArray forKey:name];
    }
    NSUUID *eventListenerUUID = [NSUUID UUID];
    [self.eventListenerByUUID setObject:eventListener forKey:eventListenerUUID];

    return eventListenerUUID;
  }
}

- (void)unregisterEventListener:(NSUUID *_Nonnull)eventListenerUUID {
  @synchronized(self) {
    ElectrodeBridgeEventListener eventListener =
        [self.eventListenerByUUID objectForKey:eventListenerUUID];
    [self.eventListenerByUUID removeObjectForKey:eventListenerUUID];

    if (eventListener) {
      NSArray *keys = [self.eventListenersByEventName allKeys];
      for (NSString *key in keys) {
        NSMutableArray *eventListeners =
            [self.eventListenersByEventName objectForKey:key];
        if ([eventListeners containsObject:eventListener]) {
          [eventListeners removeObject:eventListener];
        }
        [self.eventListenersByEventName setObject:eventListeners forKey:key];
      }
    }
  }
}

- (NSArray<ElectrodeBridgeEventListener> *_Nullable)getEventListnersForName:
    (NSString *_Nonnull)name {
  @synchronized(self) {
    NSArray<ElectrodeBridgeEventListener> *eventListeners = nil;

    if ([self.eventListenersByEventName objectForKey:name]) {
      id tempListeners = [self.eventListenersByEventName objectForKey:name];
      if ([tempListeners isKindOfClass:[NSArray class]]) {
        eventListeners = (NSArray<ElectrodeBridgeEventListener> *)[NSArray
            arrayWithArray:tempListeners];
      }
    }
    return eventListeners;
  }
}

- (NSMutableDictionary *)eventListenersByEventName {
  // Lazy instantiation
  if (!_eventListenersByEventName) {
    _eventListenersByEventName = [[NSMutableDictionary alloc] init];
  }

  return _eventListenersByEventName;
}

- (NSMutableDictionary *)eventListenerByUUID {
  // Lazy instantiation
  if (!_eventListenerByUUID) {
    _eventListenerByUUID = [[NSMutableDictionary alloc] init];
  }

  return _eventListenerByUUID;
}

@end
