/*
 * Copyright 2017 WalmartLabs
 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
