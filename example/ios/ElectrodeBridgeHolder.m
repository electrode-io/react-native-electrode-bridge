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

- (ElectrodeEventRegistrar *)eventRegistrar
{
  return self.bridge.eventRegistrar;
}

- (void)setBridge:(ElectrodeBridge *)bridge
{
  _bridge = bridge;
  
  // Let everyone know we have a bridge if not nil
  if (bridge)
  {
    for (ElectrodeBridgeHolderListener listener in _listenerBlocks)
    {
      listener(bridge);
    }
  }
}

- (void)registerStartupListener:(ElectrodeBridgeHolderListener)listenerBlock
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

- (NSString *)registerRequestHandler:(NSString *)name requestHandler:(id<ElectrodeRequestHandler>)handler error:(NSError **)error
{
  return [self.bridge.requestRegistrar registerRequestHandler:name requestHandler:handler error:error];
}

@end
