//
//  ElectrodeRequestHandlerProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/3/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class ElectrodeRequestHandlerProcessor<TReq, TResp>: NSObject, Processor {
    let tag: String
    let requestName: String
    let reqClass: TReq.Type
    let respClass: TResp.Type
    let requestHandler: ElectrodeBridgeRequestHandler
    
    public init(requestName: String,
         reqClass: TReq.Type,
         respClass: TResp.Type,
         requestHandler: ElectrodeBridgeRequestHandler)
    {
        self.tag = String(describing: type(of:self))
        self.requestName = requestName
        self.reqClass = reqClass
        self.respClass = respClass
        self.requestHandler = requestHandler
        super.init()
    }
    
    public func execute() {
        let intermediateRequestHandler = ElectrodeBridgeRequestHandlerImpt(requestClass:reqClass , requestHandler: requestHandler)
        ElectrodeBridgeHolderNew.registerRequestHanlder(withName: requestName, requestHandler: intermediateRequestHandler)
    }
    
    
}

class ElectrodeBridgeRequestHandlerImpt<TReq>: NSObject, ElectrodeBridgeRequestHandler {
    let requestClass: TReq.Type
    let requestHandler: ElectrodeBridgeRequestHandler
    
    init(requestClass: TReq.Type, requestHandler: ElectrodeBridgeRequestHandler) {
        self.requestClass = requestClass
        self.requestHandler = requestHandler
    }
    func onRequest(_ data: Any?, responseListener: ElectrodeBridgeResponseListener) {
        let request: Any?
        if let nonnilData = data {
            request = try? ElectrodeUtilities.generateObject(data: nonnilData, classType: requestClass)
        } else {
            request = nil
        }
        let innerResponseListner = InnerElectrodeBridgeResponseListener(sucessClosure:{ (any) in
            responseListener.onSuccess(any)
        }, failureClosure: { (failureMessage) in
            responseListener.onFailure(failureMessage)
        })
        
        //this is passed back to Native side. 
        requestHandler.onRequest(request, responseListener: innerResponseListner)
    }
}

class InnerElectrodeBridgeResponseListener: NSObject, ElectrodeBridgeResponseListener {
    let success: (Any?) ->()
    let failure: (ElectrodeFailureMessage) -> ()
    init(sucessClosure: @escaping (Any?)->(), failureClosure:@escaping (ElectrodeFailureMessage) -> ()) {
        success = sucessClosure
        failure = failureClosure
        super.init()
    }
    
    func onSuccess(_ responseData: Any?) {
        success(responseData)
    }
    
    func onFailure(_ failureMessage: ElectrodeFailureMessage) {
        failure(failureMessage)
    }
}
