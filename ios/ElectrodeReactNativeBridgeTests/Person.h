//
//  Person.h
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 4/10/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Person : NSObject

@property (nonatomic, strong) NSDictionary* attributes;

- (instancetype)initWithAttributes:(NSDictionary *)attributes;

@end
