//
//  ElectrodeRequestProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/10/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

typealias ElectrodeRequestProcessorSuccessClosure = ([AnyHashable: Any]?) -> ()
typealias ElectrodeRequestProcessorFailureClosure = (ElectrodeFailureMessage) -> ()

class ElectrodeRequestProcessor<TReq: Bridgeable, TResp, TItem>: NSObject {
    private let tag: String
    private let requestName: String
    private let requestPayload: TReq?
    private let responseClass: TResp.Type
    private let responseItemType: TItem.Type?
    private let responseListener: ElectrodeBridgeResponseListener
    
    init(requestName: String,
         requestPayload: TReq?,
         respClass: TResp.Type,
         responseItemType: TItem.Type?,
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
    
    func execute()
    {
        print("RequestProcessor started processing request (\(requestName)) with payload (\(requestPayload))")
        guard let requestDictionary = requestPayload?.toDictionary() as? [AnyHashable: Any] else {
            assertionFailure("\(tag) : Cannot convert payload to valid type")
            return
        }
        let request = ElectrodeBridgeRequestNew.createRequest(withData: requestDictionary)
        
        guard let validRequest = request else {
            assertionFailure("Invalid request")
            return
        }
        
        let intermediateListener = ElectrodeBridgeResponseListenerImpl(successClosure: { [weak self]
            (responseData: [AnyHashable: Any]?) in
            let processedResp = self?.processSuccessResponse(responseData: responseData)
            self?.responseListener.onSuccess(processedResp)
            
        }, failureClosure: { [weak self](failureMessage: ElectrodeFailureMessage) in
            self?.responseListener.onFailure(failureMessage)
        })
        
        ElectrodeBridgeHolderNew.sharedInstance().sendRequest(validRequest, responseListener: intermediateListener)
    }
    
    private func processSuccessResponse(responseData: [AnyHashable: Any]?) -> Any? {
        guard let dict = responseData else {
            return nil
        }
        //extract data out from BridgeMessage
        guard let dataFromResp = dict[kElectrodeBridgeMessageData] else {
            assertionFailure("NO data associated with the bridge message")
            return nil
        }
        let generatedRes: Any?
        do {
            generatedRes = try NSObject.generateObject(data: dataFromResp, classType: responseClass, itemType: responseItemType)

        } catch {
            assertionFailure("Failed to convert responseData to valid obj")
            generatedRes = nil
        }
       
        return generatedRes
    }
}

class ElectrodeBridgeResponseListenerImpl: NSObject, ElectrodeBridgeResponseListener {
    private let successClosure: ElectrodeRequestProcessorSuccessClosure?
    private let failureClosure: ElectrodeRequestProcessorFailureClosure?
    init(successClosure: ElectrodeRequestProcessorSuccessClosure?, failureClosure: ElectrodeRequestProcessorFailureClosure?)
    {
        self.successClosure = successClosure
        self.failureClosure = failureClosure
    }
    
    func onSuccess(_ responseData: Any?) { //bridge passes responseData back
        guard let success = successClosure else {
            assertionFailure("no success block on sucess callback ")
            return
        }
        
        guard responseData != nil else {
            success(nil)
            return
        }
        guard let dict = responseData as? [AnyHashable: Any] else {
            assertionFailure("ElectrodeBridgeResponseListenerImpl: wrong type ")
            return
        }
        success(dict)
    }
    
    
    func onFailure(_ failureMessage: ElectrodeFailureMessage) {
        guard let failure = failureClosure else {
            assertionFailure("no failure block on sucess callback ")
            return
        }
        failure(failureMessage)
    }
}
