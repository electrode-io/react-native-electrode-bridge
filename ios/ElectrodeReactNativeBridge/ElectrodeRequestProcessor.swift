//
//  ElectrodeRequestProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/10/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

typealias ElectrodeRequestProcessorSuccessClosure = (Any?) -> ()
typealias ElectrodeRequestProcessorFailureClosure = (ElectrodeFailureMessage) -> ()

public class ElectrodeRequestProcessor<TReq, TResp, TItem>: NSObject {
    private let tag: String
    private let requestName: String
    private let requestPayload: Any?
    private let responseClass: TResp.Type
    private let responseItemType: Any.Type?
    private let responseListener: ElectrodeBridgeResponseListener
    
    public init(requestName: String,
         requestPayload: Any?,
         respClass: TResp.Type,
         responseItemType: Any.Type?,
         responseListener: ElectrodeBridgeResponseListener)
    {
        self.tag              = String(describing: type(of:self))
        self.requestName      = requestName
        self.requestPayload   = requestPayload
        self.responseClass    = respClass
        self.responseItemType = responseItemType
        self.responseListener = responseListener //responseListnerOntheAppSide
        super.init()
    }
    
    public func execute()
    {
        print("RequestProcessor started processing request (\(requestName)) with payload (\(requestPayload))")
        let requestDictionary: [AnyHashable: Any]
        let bridgeMessageData = ElectrodeUtilities.convertObjectToBridgeMessageData(object: requestPayload)
        
        let validRequest = ElectrodeBridgeRequestNew(name: requestName, data: bridgeMessageData)
        let intermediateListener = ElectrodeBridgeResponseListenerImpl(successClosure: { [weak self]
            (responseData: Any?) in
            print("in sucess block")
            let processedResp: Any?
            if (self?.responseClass != None.self) {
                processedResp = self?.processSuccessResponse(responseData: responseData)
            } else {
                processedResp = nil
            }
            self?.responseListener.onSuccess(processedResp)
            
        }, failureClosure: {[weak self](failureMessage: ElectrodeFailureMessage) in
            print("in processor failure")
            print("\(String(describing: self))")
            self?.responseListener.onFailure(failureMessage)
            }, processor: self)
        ElectrodeBridgeHolderNew.sendRequest(validRequest, responseListener: intermediateListener)
    }
    
    private func processSuccessResponse(responseData: Any?) -> Any? {
        guard let anyData = responseData else {
            return nil
        }

        let generatedRes: Any?
        do {
            generatedRes = try NSObject.generateObject(data: anyData, classType: responseClass, itemType: responseItemType)

        } catch {
            assertionFailure("Failed to convert responseData to valid obj")
            generatedRes = nil
        }
       
        return generatedRes
    }
    
    deinit {
        print("***** ElectrodeRequestProcessor is deinited")
    }
}

class ElectrodeBridgeResponseListenerImpl<TReq, TResp, TItem>: NSObject, ElectrodeBridgeResponseListener {
    private let successClosure: ElectrodeRequestProcessorSuccessClosure?
    private let failureClosure: ElectrodeRequestProcessorFailureClosure?
    private let processor: ElectrodeRequestProcessor<TReq, TResp, TItem>?
    init(successClosure: ElectrodeRequestProcessorSuccessClosure?, failureClosure: ElectrodeRequestProcessorFailureClosure?, processor: ElectrodeRequestProcessor<TReq, TResp, TItem>)
    {
        self.successClosure = successClosure
        self.failureClosure = failureClosure
        self.processor = processor
    }
    
    func onSuccess(_ responseData: Any?) { //bridge passes responseData back
        guard let success = successClosure else {
            assertionFailure("no success block on sucess callback ")
            return
        }
        
        success(responseData)
    }
    
    
    func onFailure(_ failureMessage: ElectrodeFailureMessage) {
        guard let failure = failureClosure else {
            assertionFailure("no failure block on sucess callback ")
            return
        }
        failure(failureMessage)
    }
    deinit {
        print("***** ElectrodeBridgeResponseListenerImpl are deinited")
    }
}
