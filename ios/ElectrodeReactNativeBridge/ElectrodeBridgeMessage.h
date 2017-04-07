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
@property(nonatomic, strong, readonly, nullable) id data; //this could be NSDictionary, primitives, or NSArray

+(BOOL)isValidFromData: (NSDictionary *)data;
+(BOOL)isValidFromData: (NSDictionary *)data withType: (ElectrodeMessageType) type;
+(NSString *)UUID;

-(instancetype)initWithName: (NSString *) name
                  messageId: (NSString *)messageId
                       type: (ElectrodeMessageType) type
                       data: (id _Nullable) data;

- (instancetype)initWithName:(NSString *)name
                       type:(ElectrodeMessageType)type
                       data:(id _Nullable)data; //this could be NSDictionary, primitives, or NSArray
/*
 * return an instance of bridge message from a NSDictionary representation of it.
 * @param data NSDictionary representation of BridgeMessage. Has keys of 'id','name','type','data'
 */
-(nullable instancetype)initWithData: (NSDictionary *)data;
+(ElectrodeMessageType) typeFromString: (NSString *)string;
+ (NSString*)convertEnumTypeToString:(ElectrodeMessageType)electrodeMessageType;
-(NSDictionary *)toDictionary;

@end

NS_ASSUME_NONNULL_END
