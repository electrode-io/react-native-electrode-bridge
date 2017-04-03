//
//  ElectrodeBridgeMessage.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/20/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeMessage.h"
#import <ElectrodeReactNativeBridge/ElectrodeReactNativeBridge-Swift.h>

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
@property(nonatomic, strong, nullable) id data;
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
                       data: (id _Nullable) data
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
    if ([ElectrodeBridgeMessage isValidFromData:data]) {
        NSString *name = [data objectForKey:kElectrodeBridgeMessageName];
        NSString *messageId = [data objectForKey:kElectrodeBridgeMessageId];
        ElectrodeMessageType type = [ElectrodeBridgeMessage typeFromString:(NSString *)[data objectForKey:kElectrodeBridgeMessageType]];
        // BridgeMessage can be sent from either Native or React Native side. When it's from RN side, it can be
        // NSDictionary, primitives, NSArray etc; when it's from Native side, it will be a complex object. 
        id bridgeMessageData = [data objectForKey:kElectrodeBridgeMessageData]; //TODO: if it's primitive..?
        return [self initWithName:name messageId:messageId type:type data:bridgeMessageData];
    }
    return nil;
}
-(NSDictionary *)toDictionary {
    NSMutableDictionary *messageDict = [[NSMutableDictionary alloc] init];
    [messageDict setObject:self.name forKey:kElectrodeBridgeMessageName];
    [messageDict setObject:self.messageId forKey:kElectrodeBridgeMessageId];
    NSString *typeString = [ElectrodeBridgeMessage convertEnumTypeToString:self.type];
    [messageDict setObject:typeString forKey:kElectrodeBridgeMessageType];
    if(self.data != nil) {
        id dataObj;
        if ([self.data conformsToProtocol:@protocol(Bridgeable) ]) {
            id<Bridgeable> bridgeableData = self.data;
            dataObj = [bridgeableData toDictionary];
        } else if ([self.data isKindOfClass:[NSArray class]]) {
            id element = [self.data firstObject];
            if (element) { //assume the array has the same type of object
                if ([element conformsToProtocol:@protocol(Bridgeable)]) {
                    NSArray *convertedArray = [self convertToArrayOfBridgeable:self.data];
                    dataObj = convertedArray;
                }
            } else {
                NSLog(@"ElectrodeBridgeMessage: empty array");
            }
        } else {
            dataObj = self.data;
        }
        [messageDict setObject:dataObj forKey:kElectrodeBridgeMessageData];
    }
    
    return [messageDict copy];
}

-(NSArray<NSDictionary *> *)convertToArrayOfBridgeable: (NSArray<Bridgeable> *)data {
    NSMutableArray *res = [[NSMutableArray alloc] init];
    for (id element in data) {
        if ([element conformsToProtocol:@protocol(Bridgeable)]) {
            NSDictionary *serialized = [element toDictionary];
            [res addObject:serialized];
        } else {
            NSLog(@"ElectrodeBridgeMessage: element does not conform to protocol in toDictionary");
        }
    }
    return [res copy];
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
