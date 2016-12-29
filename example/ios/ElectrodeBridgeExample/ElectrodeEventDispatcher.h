//
//  ElectrodeEventDispatcher.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/19/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol ElectrodeEventListener <NSObject>

- (void)onEvent:(NSDictionary *)data;

@end


@class ElectrodeEventRegistrar;

@interface ElectrodeEventDispatcher : NSObject

@property (nonatomic, strong) ElectrodeEventRegistrar *eventRegistrar;

- (void)dispatchEvent:(NSString *)event id:(NSString *)id data:(NSDictionary *)data;
@end
