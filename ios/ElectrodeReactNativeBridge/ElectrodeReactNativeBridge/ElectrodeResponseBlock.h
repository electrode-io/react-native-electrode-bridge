//
//  ElectrodeResponseBlock.h
//  ElectrodeReactNativeBridge
//
//  Created by Bharath Marulasiddappa on 3/16/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ElectrodeRequestDispatcher.h"

@interface ElectrodeResponseBlock : NSObject <ElectrodeRequestCompletionListener>

typedef void (^ResponseSuccessBlock) (NSDictionary* data);
typedef void (^ResponseFailureBlock) (NSString* errorCode, NSString* errorMessage);

- (instancetype)initWithSuccess:(ResponseSuccessBlock)successBlock failureBlock:(ResponseFailureBlock)failureBlock;

@property (nonatomic, copy) ResponseSuccessBlock successBlock;
@property (nonatomic, copy) ResponseFailureBlock failureBlock;



@end
