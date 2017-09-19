//
//  ElectrodeBridgeFailureMessage.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/21/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol ElectrodeFailureMessage <NSObject>

@property(readonly, copy, nonatomic) NSString *code;
@property(readonly, copy, nonatomic) NSString *message;

@optional
@property(readonly, copy, nonatomic, nullable) NSString *debugMessage;
@property(readonly, copy, nonatomic, nullable) NSException *exception;

@end

@interface ElectrodeBridgeFailureMessage : NSObject <ElectrodeFailureMessage>

@property(readonly, copy, nonatomic) NSString *code;
@property(readonly, copy, nonatomic) NSString *message;
@property(readonly, copy, nonatomic, nullable) NSString *debugMessage;
@property(readonly, copy, nonatomic, nullable) NSException *exception;

+ (instancetype)createFailureMessageWithCode:(NSString *)code
                                     message:(NSString *)message;
+ (instancetype)createFailureMessageWithCode:(NSString *)code
                                     message:(NSString *)message
                                   exception:(nullable NSException *)exception;
+ (instancetype)createFailureMessageWithCode:(NSString *)code
                                     message:(NSString *)message
                                debugMessage:(nullable NSString *)debugMessage;

@end

NS_ASSUME_NONNULL_END
