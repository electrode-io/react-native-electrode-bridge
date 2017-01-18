//
//  ElectrodeEventRegistrar.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/20/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "ElectrodeEventRegistrar.h"
#import "ElectrodeEventDispatcher.h"

@interface ElectrodeEventRegistrar ()

@property (nonatomic, strong) NSMutableDictionary *eventListenerByUUID;
@property (nonatomic, strong) NSMutableDictionary *eventListenersByEventName;
@end

@implementation ElectrodeEventRegistrar

/**
 * Registers an event listener
 *
 * @param name The event name this listener is interested in
 * @param eventListener The event listener
 * @return A UUID to pass back to unregisterEventListener
 */
- (NSString *)registerEventListener:(NSString *)name eventListener:(id<ElectrodeEventListener>)eventListener
{
  if ([self.eventListenersByEventName objectForKey:name])
  {
    NSMutableArray *eventListenerArray = [self.eventListenersByEventName objectForKey:name];
    [eventListenerArray addObject:eventListener];
    [self.eventListenersByEventName setValue:eventListenerArray forKey:name];
  }
  else
  {
    NSMutableArray *eventListenerArray = [[NSMutableArray alloc] init];
    [eventListenerArray addObject:eventListener];
    [self.eventListenersByEventName setObject:eventListenerArray forKey:name];
  }
  NSString *eventListenerUUID = [[NSUUID UUID] UUIDString];
  [self.eventListenerByUUID setObject:eventListener forKey:eventListenerUUID];
  
  return eventListenerUUID;
}

/**
 * Unregisters an event listener
 *
 * @param eventListenerUuid The UUID that was obtained through initial registerEventListener
 * call
 */
- (void)unregisterEventListener:(NSString *)eventListenerUUID
{
  // Grab and remove the listener from the UUID storage
  id<ElectrodeEventListener> eventListener = [self.eventListenerByUUID objectForKey:eventListenerUUID];
  [self.eventListenerByUUID removeObjectForKey:eventListenerUUID];
  
  
  if (eventListener && [eventListener conformsToProtocol:@protocol(ElectrodeEventListener)])
  {
    NSArray *keys = [self.eventListenersByEventName allKeys];
    for (NSString *key in keys)
    {
      NSMutableArray *eventListeners = [self.eventListenersByEventName objectForKey:key];
      if ([eventListeners containsObject:eventListener])
      {
        [eventListeners removeObject:eventListener];
      }
      [self.eventListenersByEventName setObject:eventListeners forKey:key];
    }
  }
}

/**
 * Gets the list of all event listeners registered for a given event name
 *
 * @param name The event name
 * @return A list of event listeners registered for the given event name or an empty list if no
 * event listeners are currently registered for this event name
 */
- (NSArray<ElectrodeEventListener> *)getEventListnersForName:(NSString *)name
{
  NSArray<ElectrodeEventListener> *eventListeners = nil;
  
  if ([self.eventListenersByEventName objectForKey:name])
  {
    id tempListeners = [self.eventListenersByEventName objectForKey:name];
    if ([tempListeners isKindOfClass:[NSArray class]])
    {
      eventListeners = (NSArray<ElectrodeEventListener> *)[NSArray arrayWithArray:tempListeners];
    }
  }
  return eventListeners;
}

- (NSMutableDictionary *)eventListenersByEventName
{
  // Lazy instantiation
  if (!_eventListenersByEventName)
  {
    _eventListenersByEventName = [[NSMutableDictionary alloc] init];
  }
  
  return _eventListenersByEventName;
}

- (NSMutableDictionary *)eventListenerByUUID
{
  // Lazy instantiation
  if (!_eventListenerByUUID)
  {
    _eventListenerByUUID = [[NSMutableDictionary alloc] init];
  }
  
  return _eventListenerByUUID;
}

@end
