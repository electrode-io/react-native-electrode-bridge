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
    private let completion: ElectrodeBridgeResponseCompletionBlock
    
    public init(requestName: String,
         requestPayload: Any?,
         respClass: TResp.Type,
         responseItemType: Any.Type?,
         completion: @escaping ElectrodeBridgeResponseCompletionBlock)
    {
        self.tag              = String(describing: type(of:self))
        self.requestName      = requestName
        self.requestPayload   = requestPayload
        self.responseClass    = respClass
        self.responseItemType = responseItemType
        self.completion = completion
        super.init()
    }
    
    public func execute() {
        print("RequestProcessor started processing request (\(requestName)) with payload (\(String(describing: requestPayload)))")
        let bridgeMessageData = ElectrodeUtilities.convertObjectToBridgeMessageData(object: requestPayload)
        
        let validRequest = ElectrodeBridgeRequestNew(name: requestName, data: bridgeMessageData)

        ElectrodeBridgeHolderNew.sendRequest(validRequest) { (responseData: Any?, failureMessage: ElectrodeFailureMessage?) in
            if let failureMessage = failureMessage {
                self.completion(nil, failureMessage)
            } else {
                let processedResp: Any?
                if (self.responseClass != None.self) {
                    processedResp = self.processSuccessResponse(responseData: responseData)
                } else {
                    processedResp = nil
                }
                self.completion(processedResp, nil)
            }
        }
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
}
