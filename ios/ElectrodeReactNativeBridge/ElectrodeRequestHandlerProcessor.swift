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
    let requestCompletionHandler: ElectrodeBridgeRequestCompletionHandler
    
    public init(requestName: String,
         reqClass: TReq.Type,
         respClass: TResp.Type,
         requestCompletionHandler: @escaping ElectrodeBridgeRequestCompletionHandler)
    {
        self.tag = String(describing: type(of:self))
        self.requestName = requestName
        self.reqClass = reqClass
        self.respClass = respClass
        self.requestCompletionHandler = requestCompletionHandler
        super.init()
    }
    
    public func execute() {
        ElectrodeBridgeHolder.registerRequestHanlder(withName: requestName) {(data: Any?, responseCompletion: @escaping ElectrodeBridgeResponseCompletionHandler) in
            let request: Any?
            if (self.reqClass == None.self) {
                request = nil
            } else {
                if let nonnilData = data {
                    request = try? ElectrodeUtilities.generateObject(data: nonnilData, classType: self.reqClass)
                } else {
                    request = nil
                }
            }
            
            //this is passed back to Native side.
            self.requestCompletionHandler(request, responseCompletion)
        }
    }
}
