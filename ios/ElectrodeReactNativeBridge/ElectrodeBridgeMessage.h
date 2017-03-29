//
//  ElectrodeBridgeMessage.h
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/20/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN

extern NSString * const kElectrodeBridgeMessageName;
extern NSString * const kElectrodeBridgeMessageId;
extern NSString * const kElectrodeBridgeMessageType;
extern NSString * const kElectrodeBridgeMessageData;

extern NSString * const kElectrodeBridgeMessageRequest;
extern NSString * const kElectrodeBridgeMessageResponse;
extern NSString * const kElectordeBridgeMessageEvent;

typedef NS_ENUM(NSUInteger, ElectrodeMessageType) {
    ElectrodeMessageTypeRequest,
    ElectrodeMessageTypeResponse,
    ElectrodeMessageTypeEvent,
    ElectrodeMessageTypeUnknown
};

@interface ElectrodeBridgeMessage : NSObject

@property(nonatomic, copy, readonly) NSString *name;
@property(nonatomic, copy, readonly) NSString *messageId;
@property(nonatomic, assign, readonly)ElectrodeMessageType type;
@property(nonatomic, copy, readonly) NSDictionary *data;

+(BOOL)isValidFromData: (NSDictionary *)data;
+(BOOL)isValidFromData: (NSDictionary *)data withType: (ElectrodeMessageType) type;
+(NSString *)UUID;

-(instancetype)initWithName: (NSString *) name
                  messageId: (NSString *)messageId
                       type: (ElectrodeMessageType) type
                       data: (NSDictionary *) data;

-(nullable instancetype)initWithData: (NSDictionary *)data;
-(NSString *)description;
+(ElectrodeMessageType) typeFromString: (NSString *)string;

@end

NS_ASSUME_NONNULL_END
