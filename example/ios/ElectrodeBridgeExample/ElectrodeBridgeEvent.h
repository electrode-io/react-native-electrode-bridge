//
//  ElectrodeBridgeEvent.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 12/16/16.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ElectrodeBridge.h"

@interface ElectrodeBridgeEvent : NSObject

@property (nonatomic, readonly) NSString *name;
@property (nonatomic, readonly) NSDictionary *data;
@property (nonatomic, readonly) EBDispatchMode dispatchMode;

- (instancetype)initWithName:(NSString *)name data:(NSDictionary *)data mode:(EBDispatchMode)mode;
@end
