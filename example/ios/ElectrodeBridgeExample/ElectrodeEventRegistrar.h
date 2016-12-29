//
//  ElectrodeEventRegistrar.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/20/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol ElectrodeEventListener;

@interface ElectrodeEventRegistrar : NSObject

- (NSString *)registerEventListener:(NSString *)name eventListener:(id<ElectrodeEventListener>)eventListener;

- (void)unregisterEventListener:(NSString *)eventListenerUUID;

- (NSArray<ElectrodeEventListener> *)getEventListnersForName:(NSString *)name;

@end
