//
//  Person.m
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 4/10/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "Person.h"

@implementation Person

- (instancetype)initWithAttributes:(NSDictionary *)attributes {
    if (self = [super init]) {
        self.attributes = attributes;
    }
    return self;
}

- (id)copyWithZone:(NSZone *)zone {
    id personObject = [[[self class] alloc] init];
    if (personObject) {
        [personObject setAttributes:[self.attributes copyWithZone:zone]];
    }
    return personObject;
}

@end
