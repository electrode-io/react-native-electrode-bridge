//
//  ElectrodeBridgeTransceiver.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransceiver.h"
#import "ElectrodeEventDispatcherNew.h"
#import "ElectrodeRequestDispatcherNew.h"
#import "ElectrodeBridgeTransaction.h"
#import "ElectrodeEventRegistrarNew.h"
#import "ElectrodeRequestRegistrarNew.h"
#import <React/RCTLog.h>
#import <React/RCTBridge.h>
#import "ElectrodeBridgeMessage.h"
#import "ElectrodeBridgeEventNew.h"

NS_ASSUME_NONNULL_BEGIN

@interface ElectrodeBridgeResponseListenerImplementor : NSObject<ElectrodeBridgeResponseListener>

@property(nonatomic, copy) ElectrodeBridgeResponseListenerSuccessBlock successBlock;
@property(nonatomic, copy) ElectrodeBridgeResponseListenerFailureBlock failureBlock;

-(instancetype)initWithSuccessBlock: (ElectrodeBridgeResponseListenerSuccessBlock) successBlock
                       failureBlock: (ElectrodeBridgeResponseListenerFailureBlock) failureBlock;
@end

@implementation ElectrodeBridgeResponseListenerImplementor

-(instancetype)initWithSuccessBlock: (ElectrodeBridgeResponseListenerSuccessBlock) successBlock
                       failureBlock: (ElectrodeBridgeResponseListenerFailureBlock) failureBlock {
    if (self = [super init]) {
        _successBlock = successBlock;
        _failureBlock = failureBlock;
    }
    return self;
}

-(void)onFailure:(id<ElectrodeFailureMessage>)failureMessage
{
    if (self.failureBlock) {
        self.failureBlock(failureMessage);
    }
}

-(void)onSuccess:(NSDictionary * _Nullable)responseData
{
    if(self.successBlock) {
        self.successBlock(responseData);
    }
}

@end

@interface ElectrodeBridgeTransceiver()

@property(nonatomic, copy) NSString *name;
@property(nonatomic, strong) ElectrodeEventDispatcherNew *eventDispatcher;
@property(nonatomic, strong) ElectrodeRequestDispatcherNew *requestDispatcher;
@property(nonatomic, copy) NSMutableDictionary<NSString *, ElectrodeBridgeTransaction * > *pendingTransaction;
@property (nonatomic, assign) dispatch_queue_t syncQueue; //this is used to make sure access to pendingTransaction is thread safe.
@property(nonatomic, copy) NSMutableArray *listnerBlocks;

@end
//CLAIRE TODO: check what are the methods that needs to mark with RCT_EXPORT_METHOD 
@implementation ElectrodeBridgeTransceiver

+(instancetype)sharedInstance {
    static ElectrodeBridgeTransceiver *sharedInstance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        sharedInstance = [[ElectrodeBridgeTransceiver alloc] init];
    });
    return sharedInstance;
}

-(instancetype)init {
    if (self = [super init])
    {
        ElectrodeRequestRegistrarNew *requestRegistrar = [[ElectrodeRequestRegistrarNew alloc] init];
        ElectrodeEventRegistrarNew *eventRegistrar = [[ElectrodeEventRegistrarNew alloc] init];
        _requestDispatcher = [[ElectrodeRequestDispatcherNew alloc] initWithRequestRegistrar:requestRegistrar];
        _eventDispatcher = [[ElectrodeEventDispatcherNew alloc] initWithEventRegistrar:eventRegistrar];
        _pendingTransaction = [[NSMutableDictionary alloc] init];
        _listnerBlocks = [[NSMutableArray alloc] init];
    }
    return self;
}
RCT_EXPORT_MODULE();
+ (NSArray *)electrodeModules
{
    return @[[[ElectrodeBridgeTransceiver alloc] init]];
}

-(NSString *) name {
    return @"ElectrodeNativeBridge";
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma ElectrodeNativeBridge implementation
-(void)sendRequest:(ElectrodeBridgeRequestNew *)request withResponseListener:(id<ElectrodeBridgeResponseListener> _Nonnull) responseListener {
    [self handleRequest:request withResponseListner:responseListener];
}

-(void)regiesterRequestHandlerWithName: (NSString *)name handler:(id<ElectrodeBridgeRequestHandler>) requestHandler error:(NSError * _Nullable __autoreleasing * _Nullable)error{
    [self.requestDispatcher.requestRegistrar registerRequestHandler:name requestHandler:requestHandler error:error]; //CLAIRE TODO: Check if need to return UUID
}


-(void)sendEvent: (ElectrodeBridgeEventNew *)event {
    NSLog(@"ElectrodeBridgeTransceiver: emit event named: %@, id: %@", event.name, event.messageId);
    [self notifyNativeEventListenerWithEvent:event];
    [self notifyReactNativeEventListenerWithEvent:event];
}

-(NSUUID *)addEventListenerWithName: (NSString *)name eventListener: (id<ElectrodeBridgeEventListener>) eventListener {
    NSLog(@"%@, Adding eventListener %@ for event %@", NSStringFromClass([self class]), eventListener, name);
    NSUUID *uUID = [self.eventDispatcher.eventRegistrar registerEventListener:name eventListener:eventListener];
    return uUID;
}
#pragma ElectrodeReactBridge

RCT_EXPORT_METHOD(sendMessage:(NSDictionary *)bridgeMessage)
{
    NSLog(@"Received message from JS(data=%@)", bridgeMessage);
    NSString *typeString = (NSString *)[bridgeMessage objectForKey:kElectrodeBridgeMessageType];
    ElectrodeMessageType type = [ElectrodeBridgeMessage typeFromString:typeString];
    switch (type) {
        case ElectrodeMessageTypeRequest:
        {
            ElectrodeBridgeRequestNew *request = [ElectrodeBridgeRequestNew createRequestWithData:bridgeMessage];
            [self handleRequest:request withResponseListner:nil];
            break;
        }
            
        case ElectrodeMessageTypeResponse:
        {
            ElectrodeBridgeResponse *response = [ElectrodeBridgeResponse createResponseWithData:bridgeMessage];
            if (response != nil) {
                [self handleResponse:response];
            } else {
                [NSException raise:@"invalue resonse" format:@"cannot construct a response from data"];
            }
            break;
        }
        case ElectrodeMessageTypeEvent:
        {
            ElectrodeBridgeEventNew *event = [ElectrodeBridgeEventNew createEventWithData:bridgeMessage];
            if (event != nil) {
                [self notifyNativeEventListenerWithEvent:event];
            } else {
                [NSException raise:@"invalue event" format:@"cannot construct an event from data"];
                
            }
            break;
        }
        case ElectrodeMessageTypeUnknown:
        default:
            [NSException raise:@"invalue message" format:@"cannot construct any message from data"];
            break;
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma private methods

-(void)notifyReactNativeEventListenerWithEvent: (ElectrodeBridgeEventNew *) event {
    [self sendEventWithName:event.name body:event.data]; //note: don't use the old emitEvent because it's deprecated according to RCT docs
}

-(void)notifyNativeEventListenerWithEvent: (ElectrodeBridgeEventNew *)event {
    [self.eventDispatcher dispatchEvent:event];
}

-(void)handleRequest:(ElectrodeBridgeRequestNew *)request withResponseListner:(id<ElectrodeBridgeResponseListener> _Nullable) responseListener
{
    [self logRequest:request];
    
    if (![responseListener conformsToProtocol:@protocol(ElectrodeBridgeResponseListener)]) {
        [NSException raise:@"invalid response listener" format:@"A response lister need to conform to ElectrodeBridgeResponseListener protocol"];
    }
    
    if (responseListener == nil && !request.isJsInitiated) {
        [NSException raise:@"invalid operation" format:@"A response lister is required for a native initiated request"];
    }
    ElectrodeBridgeTransaction *transaction = [[ElectrodeBridgeTransaction alloc] initWithRequest:request responseListener:responseListener];
    
    if ([self.requestDispatcher canHandlerRequestWithName:request.name] ) {
        [self dispatchRequestToNativeHandlerForTransaction:transaction];
    } else if (!request.isJsInitiated) {
        [self dispatchRequestToReactHandlerForTransaction:transaction];
    }else {
        NSLog(@"No handler available to handle request(id=%@, name=%@", request.messageId, request.name);
        id<ElectrodeFailureMessage> failureMessage = [ElectrodeBridgeFailureMessage createFailureMessageWithCode:@"ENOHANDLER" message:@"No registered request handler found"];
        ElectrodeBridgeResponse *response = [ElectrodeBridgeResponse createResponseForRequest:request withResponseData:nil withFailureMessage:failureMessage];
        [self handleResponse:response];
    }
}

-(void)dispatchRequestToNativeHandlerForTransaction: (ElectrodeBridgeTransaction *)transaction
{
    NSLog(@"Sending request(id=%@) to native handler", transaction.request.messageId);
    ElectrodeBridgeRequestNew *request = transaction.request;
    __weak ElectrodeBridgeTransceiver *weakSelf = self;

    id<ElectrodeBridgeResponseListener> responseListener = [[ElectrodeBridgeResponseListenerImplementor alloc] initWithSuccessBlock:^(NSDictionary * _Nonnull data) {
        ElectrodeBridgeResponse *response = [ElectrodeBridgeResponse createResponseForRequest:request
                                                                             withResponseData:data
                                                                           withFailureMessage:nil];
        [weakSelf handleResponse:response];
    } failureBlock:^(id<ElectrodeFailureMessage> _Nonnull message) {
        ElectrodeBridgeResponse *response = [ElectrodeBridgeResponse createResponseForRequest:request
                                                                             withResponseData:nil
                                                                           withFailureMessage:message];
        [weakSelf handleResponse:response];
    }];
    [self.requestDispatcher dispatchRequest:request withResponseListener:responseListener];
    
}

-(void)dispatchRequestToReactHandlerForTransaction:(ElectrodeBridgeTransaction *)transaction
{
    NSLog(@"Sending request(id=%@) over to JS to handler because there is no local request handler available", transaction.request.messageId);
    [self sendEventWithName:transaction.request.name body:transaction.request.data];
}

-(void)handleResponse:(ElectrodeBridgeResponse *)response
{
    NSLog(@"hanlding bridge response");
    ElectrodeBridgeTransaction *transaction;
    @synchronized (self) {
         transaction = (ElectrodeBridgeTransaction *) [self.pendingTransaction objectForKey:response.messageId];
    }
    if (transaction != nil) {
        transaction.response = response;
        [self completeTransaction:transaction];
    } else {
        NSLog(@"Response(id=%@, name=%@) will be ignored because the transcation for this request has been removed from the queue. Perhaps it's already timed-out or completed.", response.messageId, response.name);
    }
    
}

-(void)completeTransaction:(ElectrodeBridgeTransaction *)transaction
{
    if(transaction.response == nil) {
        [NSException raise:@"invalid transaction" format:@"Cannot complete transaction, a transaction can only be completed with a valid response"];
    }
    NSLog(@"completing transaction(id=%@", transaction.transactionId);

    [self.pendingTransaction removeObjectForKey:transaction.transactionId];
    
    ElectrodeBridgeResponse *response = transaction.response;
    [self logResponse:response];
    
    if(transaction.isJsInitiated) {
        NSLog(@"Completing transaction by emitting event back to JS since the request is initiated from JS side");
        [self sendEventWithName:response.name body:response.data];
    } else {
        if(transaction.finalResponseListener != nil) {
            if(response.failureMessage != nil) {
                NSLog(@"Completing transaction by issuing a failure call back to local response listener");
                dispatch_async(dispatch_get_main_queue(), ^{
                    [transaction.finalResponseListener onFailure:response.failureMessage];
                });
            } else {
                NSLog(@"Completing transaction by issuing a success call back to local response listener");
                dispatch_async(dispatch_get_main_queue(), ^{
                    [transaction.finalResponseListener onFailure:response.failureMessage];
                });
            }
        } else {
            [NSException raise:@"invalid transaction" format:@"Should never reach here. A response listener should always be set for a local transaction"];
        }
    }
}

-(void)logRequest: (ElectrodeBridgeRequestNew *)request {
    //CLAIRE TODO: Add Logs
}

-(void)logResponse: (ElectrodeBridgeResponse *)response {
    
}

- (void)registerReactNativeReadyListener: (ElectrodeBridgeReactNativeReadyListner) reactNativeReadyListner
{
    if(self.bridge) {
        reactNativeReadyListner();
    } else {
        [self.listnerBlocks addObject:[reactNativeReadyListner copy]];
    }
}


// CLAIRE TODO: initialization static method to bring up the bridge

@end



NS_ASSUME_NONNULL_END
