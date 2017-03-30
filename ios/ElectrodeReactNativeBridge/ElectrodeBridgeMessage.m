//
//  ElectrodeBridgeMessage.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/20/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeMessage.h"
NS_ASSUME_NONNULL_BEGIN

NSString * const kElectrodeBridgeMessageName = @"name";
NSString * const kElectrodeBridgeMessageId = @"id";
NSString * const kElectrodeBridgeMessageType = @"type";
NSString * const kElectrodeBridgeMessageData = @"data";

NSString * const kElectrodeBridgeMessageRequest = @"req";
NSString * const kElectrodeBridgeMessageResponse = @"rsp";
NSString * const kElectordeBridgeMessageEvent = @"event";
NSString * const kElectordeBridgeMessageUnknown = @"unknown";

@interface ElectrodeBridgeMessage()
@property(nonatomic, copy)   NSString *name;
@property(nonatomic, copy)   NSString *messageId;
@property(nonatomic, assign) ElectrodeMessageType type;
@property(nonatomic, copy)   NSDictionary *data;
@end

@implementation ElectrodeBridgeMessage

+(NSString *)UUID {
    return [[[NSUUID alloc] init] UUIDString];
}

+(BOOL)isValidFromData: (NSDictionary *)data {
    return [data objectForKey:kElectrodeBridgeMessageName] &&
            [data objectForKey:kElectrodeBridgeMessageId] &&
            [data objectForKey:kElectrodeBridgeMessageType];
}

+(BOOL)isValidFromData: (NSDictionary *)data withType: (ElectrodeMessageType) type {
    return [ElectrodeBridgeMessage isValidFromData:data] &&
    ([ElectrodeBridgeMessage typeFromString:(NSString *)[data objectForKey:kElectrodeBridgeMessageType]] == type);
}

-(instancetype)initWithName: (NSString *) name
                  messageId:(NSString *)messageId
                       type: (ElectrodeMessageType) type
                       data: (NSDictionary *) data
{
    if (self = [super init]) {
        _name      = name;
        _messageId = messageId;
        _type      = type;
        _data      = data;
    }
    
    return self;
}

- (nullable instancetype)initWithData:(NSDictionary *)data {
    if ([ElectrodeBridgeMessage isValidFromData:data]) { //CLAIRE TODO: Ask Deepu why it's not checking for data.
        NSString *name = [data objectForKey:kElectrodeBridgeMessageName];
        NSString *messageId = [data objectForKey:kElectrodeBridgeMessageId];
        ElectrodeMessageType type = [ElectrodeBridgeMessage typeFromString:(NSString *)[data objectForKey:kElectrodeBridgeMessageType]];
        NSDictionary *bridgeMessageData = (NSDictionary *)[data objectForKey:kElectrodeBridgeMessageData];
        return [self initWithName:name messageId:messageId type:type data:bridgeMessageData];
    }
    return nil;
}

+ (NSString*)convertEnumTypeToString:(ElectrodeMessageType)electrodeMessageType {
    NSString *result = nil;
    switch(electrodeMessageType) {
        case ElectrodeMessageTypeRequest:
            result = kElectrodeBridgeMessageRequest;
            break;
        case ElectrodeMessageTypeResponse:
            result = kElectrodeBridgeMessageResponse;
            break;
        case ElectrodeMessageTypeEvent:
            result = kElectordeBridgeMessageEvent;
            break;
        case ElectrodeMessageTypeUnknown:
            result = kElectordeBridgeMessageUnknown;
            break;
        default:
            [NSException raise:NSGenericException format:@"Unexpected FormatType."];
    }
    return result;
}

+ (ElectrodeMessageType) typeFromString: (NSString *)string { //CLAIRE TODO: What to do when the type string does not match
    if ([string isEqualToString:kElectrodeBridgeMessageRequest]) {
        return ElectrodeMessageTypeRequest;
    } else if ([string isEqualToString:kElectrodeBridgeMessageResponse]) {
        return ElectrodeMessageTypeResponse;
    } else if ([string isEqualToString:kElectordeBridgeMessageEvent]) {
        return ElectrodeMessageTypeEvent;
    } else {
        return ElectrodeMessageTypeUnknown;
    }
}


@end

NS_ASSUME_NONNULL_END
