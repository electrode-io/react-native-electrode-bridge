//
//  ElectrodeBridgeRequest.h
//  ElectrodeBridgeExample
//
//  Created by Cody Garvin on 1/4/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ElectrodeBridge.h"

@interface ElectrodeBridgeRequest : NSObject

@property (nonatomic, readonly) NSString *name;
@property (nonatomic, readonly) NSDictionary *data;
@property (nonatomic, readonly) EBDispatchMode dispatchMode;
@property (nonatomic, readonly) NSInteger timeout;

- (instancetype)initWithName:(NSString *)name data:(NSDictionary *)data mode:(EBDispatchMode)mode;

- (instancetype)initWithName:(NSString *)name data:(NSDictionary *)data mode:(EBDispatchMode)mode timeout:(NSInteger)timeout;
@end
