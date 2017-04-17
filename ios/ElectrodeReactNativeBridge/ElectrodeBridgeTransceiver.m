//
//  ElectrodeBridgeTransceiver.m
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 3/22/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

#import "ElectrodeBridgeTransceiver.h"
#import "ElectrodeBridgeTransceiver_Internal.h"
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


@end
@implementation ElectrodeBridgeTransceiver

+(instancetype)sharedInstance {
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
    }
    return self;
}
RCT_EXPORT_MODULE(ElectrodeBridge);
+ (NSArray *)electrodeModules
{
    return @[[[ElectrodeBridgeTransceiver alloc] init]];
}

-(NSArray *) supportedEvents
{
    return @[@"electrode.bridge.message"];
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
#pragma ElectrodeNativeBridge implementation
-(void)sendRequest:(ElectrodeBridgeRequestNew *)request withResponseListener:(id<ElectrodeBridgeResponseListener> _Nonnull) responseListener {
    [self handleRequest:request withResponseListner:responseListener];
}

-(NSUUID *)regiesterRequestHandlerWithName: (NSString *)name handler:(id<ElectrodeBridgeRequestHandler>) requestHandler error:(NSError * _Nullable __autoreleasing * _Nullable)error{
    NSUUID *uUID = [self.requestDispatcher.requestRegistrar registerRequestHandler:name requestHandler:requestHandler error:error]; //CLAIRE TODO: Check if need to return UUID
    return uUID;
}

-(void)resetRegistrar {
    [self.requestDispatcher.requestRegistrar reset];
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

-(void)emitMessage:(ElectrodeBridgeMessage * _Nonnull)bridgeMessage
{
    NSLog(@"Sending bridgeMessage(%@) to JS", bridgeMessage);
    [self sendEventWithName:@"electrode.bridge.message" body:[bridgeMessage toDictionary]];
}

-(void)notifyReactNativeEventListenerWithEvent: (ElectrodeBridgeEventNew *) event {
    [self emitMessage:event];
}

-(void)notifyNativeEventListenerWithEvent: (ElectrodeBridgeEventNew *)event {
    [self.eventDispatcher dispatchEvent:event];
}

-(void)handleRequest:(ElectrodeBridgeRequestNew *)request withResponseListner:(id<ElectrodeBridgeResponseListener> _Nullable) responseListener
{
    [self logRequest:request];
    
    if (responseListener != nil && ![responseListener conformsToProtocol:@protocol(ElectrodeBridgeResponseListener)]) {
        [NSException raise:@"invalid response listener" format:@"A response lister need to conform to ElectrodeBridgeResponseListener protocol"];
    }
    
    if (responseListener == nil && !request.isJsInitiated) {
        [NSException raise:@"invalid operation" format:@"A response lister is required for a native initiated request"];
    }
    ElectrodeBridgeTransaction *transaction = [self createTransactionWithRequest:request responseListner:responseListener];
    if ([self.requestDispatcher canHandlerRequestWithName:request.name] ) {
        [self dispatchRequestToNativeHandlerForTransaction:transaction];
    } else if (!request.isJsInitiated) { //GOTCHA: Make sure not send a request back to JS if it's initiated on JS side
        [self dispatchRequestToReactHandlerForTransaction:transaction];
    }else {
        NSLog(@"No handler available to handle request(%@)", request);
        id<ElectrodeFailureMessage> failureMessage = [ElectrodeBridgeFailureMessage createFailureMessageWithCode:@"ENOHANDLER" message:@"No registered request handler found"];
        ElectrodeBridgeResponse *response = [ElectrodeBridgeResponse createResponseForRequest:request withResponseData:nil withFailureMessage:failureMessage];
        [self handleResponse:response];
    }
}

-(ElectrodeBridgeTransaction *)createTransactionWithRequest: (ElectrodeBridgeRequestNew *)request
                    responseListner:(id<ElectrodeBridgeResponseListener> _Nullable) responseListener
{
    ElectrodeBridgeTransaction *transaction = [[ElectrodeBridgeTransaction alloc] initWithRequest:request responseListener:responseListener];
    
    @synchronized (self) {
        [self.pendingTransaction setObject:transaction forKey:request.messageId];
        [self startTimeOutCheckForTransaction:transaction];
    }
    
    return transaction;
}

-(void)startTimeOutCheckForTransaction: (ElectrodeBridgeTransaction *)transaction
{
    // Add the timeout handler
    __weak ElectrodeBridgeTransceiver *weakSelf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(transaction.request.timeoutMs * NSEC_PER_MSEC)), dispatch_get_main_queue(), ^{
        
        id<ElectrodeBridgeResponseListener> responseListener = transaction.finalResponseListener;
        if (responseListener && [responseListener conformsToProtocol:@protocol(ElectrodeBridgeResponseListener)])
        {
            id<ElectrodeFailureMessage> failureMessage = [ElectrodeBridgeFailureMessage createFailureMessageWithCode:@"TIMEOUT" message:@"transaction timed out for request"];
            ElectrodeBridgeResponse *response = [ElectrodeBridgeResponse createResponseForRequest:transaction.request withResponseData:nil withFailureMessage:failureMessage];
            [weakSelf handleResponse:response];
        }
    });
}

-(void)dispatchRequestToNativeHandlerForTransaction: (ElectrodeBridgeTransaction *)transaction
{
    NSLog(@"Sending request(%@) to native handler", transaction.request);
    ElectrodeBridgeRequestNew *request = transaction.request;
    __weak ElectrodeBridgeTransceiver *weakSelf = self;

    id<ElectrodeBridgeResponseListener> responseListener = [[ElectrodeBridgeResponseListenerImplementor alloc] initWithSuccessBlock:^(id _Nullable data) {
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
    NSLog(@"Sending request(%@) over to JS handler because there is no local request handler available", transaction.request);
    [self emitMessage:transaction.request];
}

-(void)handleResponse:(ElectrodeBridgeResponse *)response
{
    NSLog(@"hanlding bridge response: %@", response);
    ElectrodeBridgeTransaction *transaction;
    @synchronized (self) {
         transaction = (ElectrodeBridgeTransaction *) [self.pendingTransaction objectForKey:response.messageId];
    }
    if (transaction != nil) {
        transaction.response = response;
        [self completeTransaction:transaction];
    } else {
        NSLog(@"Response(%@) will be ignored because the transcation for this request has been removed from the queue. Perhaps it's already timed-out or completed.", response);
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
        [self emitMessage:response];
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
                    [transaction.finalResponseListener onSuccess:response.data];
                });
            }
        } else {
            [NSException raise:@"invalid transaction" format:@"Should never reach here. A response listener should always be set for a local transaction"];
        }
    }
}

-(void)logRequest: (ElectrodeBridgeRequestNew *)request {
    NSLog(@"--> --> --> --> --> Request(%@)", request);
}

-(void)logResponse: (ElectrodeBridgeResponse *)response {
    NSLog(@"<-- <-- <-- <-- <-- Response(%@)", response);
}

+ (void)registerReactNativeReadyListener: (ElectrodeBridgeReactNativeReadyListner) listner
{
    if(isReactNativeReady) {
        if (listner) {
            listner(sharedInstance);
        }
    }
    
    reactNativeReadyListener = [listner copy];
}

- (void)onReactNativeInitialized
{
    isReactNativeReady = YES;
    sharedInstance = self;

    if (reactNativeReadyListener) {
        reactNativeReadyListener(self);
    }
}
+ (BOOL)isReactNativeBridgeReady {
    return isReactNativeReady;
}
@end
NS_ASSUME_NONNULL_END
