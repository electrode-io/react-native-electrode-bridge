//
//  ElectrodeBridgeHolder.m
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/27/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import "ElectrodeBridgeHolder.h"
#import "ElectrodeBridge.h"
#import "ElectrodeEventRegistrar.h"
#import "ElectrodeRequestRegistrar.h"

@interface ElectrodeBridgeHolder ()
@property (nonatomic, strong) NSMutableArray *listenerBlocks;
@property (nonatomic, weak) id<ElectrodeBridgeInterface> bridge;
@end


@implementation ElectrodeBridgeHolder

+ (instancetype)sharedInstance
{
  static ElectrodeBridgeHolder *sharedInstance = nil;
  static dispatch_once_t onceToken;
  
  dispatch_once(&onceToken, ^{
    sharedInstance = [[ElectrodeBridgeHolder alloc] init];
  });
  return sharedInstance;
}

+ (NSArray *)electrodeModules
{
    return @[[[ElectrodeBridge alloc] init]];
}

- (ElectrodeEventRegistrar *)eventRegistrar
{
  return self.bridge.eventRegistrar;
}

- (void)setBridge:(id)bridge
{
  _bridge = bridge;
  
  // Let everyone know we have a bridge if not nil
  if (bridge && [bridge isKindOfClass:[ElectrodeBridge class]])
  {
    for (ElectrodeBridgeHolderListener listener in _listenerBlocks)
    {
      listener(bridge);
    }
  }
}

- (void)setOnBridgeReadyListener:(ElectrodeBridgeHolderListener)listenerBlock
{
  if (self.bridge)
  {
    listenerBlock(self.bridge);
  }
  else
  {
    if (!_listenerBlocks)
    {
      self.listenerBlocks = [[NSMutableArray alloc] init];
    }
    
    [self.listenerBlocks addObject:[listenerBlock copy]];
  }
}

- (NSString *)registerEventListener:(NSString *)name eventListener:(id<ElectrodeEventListener>)eventListener
{
  return [self.bridge.eventRegistrar registerEventListener:name eventListener:eventListener];
}

- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)requestHandler error:(NSError **)error
{
  return [self.bridge.requestRegistrar registerRequestHandler:name requestHandler:requestHandler error:error];
}

- (void)sendRequest:(ElectrodeBridgeRequest *)request completionListener:(id<ElectrodeRequestCompletionListener>)completionListener
{
    [self.bridge sendRequest:request completionListener:completionListener];
}

- (void)emitEvent:(ElectrodeBridgeEvent *)event
{
    [self.bridge emitEvent:event];
}
@end
