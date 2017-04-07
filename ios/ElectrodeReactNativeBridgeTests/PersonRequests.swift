//
//  PersonRequest.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class PersonRequests: NSObject, Request {
    func registerGetPersonRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: kRequestGetPerson, reqClass: None.self, respClass: Person.self, requestHandler: handler)
        requestHandlerProcessor.execute()
    }
    
    func registerGetStatusRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: kRequestGetStatus, reqClass: Person.self, respClass: Status.self, requestHandler: handler)
        requestHandlerProcessor.execute()
    }
    func registerGetAgeRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: kRequestGetAge, reqClass: String.self, respClass: Int.self, requestHandler: handler)
        requestHandler.execute()
    }
    
    func getPerson(responseListner responseListener: ElectrodeBridgeResponseListener) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Person, Any>(requestName: kRequestGetPerson,
                                                          requestPayload: nil,
                                                          respClass: Person.self,
                                                          responseItemType: nil,
                                                          responseListener: responseListener)
        requestProcessor.execute()
    }
    
    func getStatus(person: Person, responseListener: ElectrodeBridgeResponseListener) {
        let requestProcessor = ElectrodeRequestProcessor<Person, Status, Any>(requestName: kRequestGetStatus,                                                                                requestPayload: person, respClass: Status.self, responseItemType: nil, responseListener: responseListener)
        requestProcessor.execute()
    }
    
    func getUserName(responseListner: ElectrodeBridgeResponseListener) {
        let requestProcessor = ElectrodeRequestProcessor<None, String, Any>(requestName: kRequestGetUserName,
                                                                              requestPayload: nil,
                                                                              respClass: String.self,
                                                                              responseItemType: nil,
                                                                              responseListener: responseListner)
        requestProcessor.execute()

    }
    
    func getAge(name: String, responseListener: ElectrodeBridgeResponseListener) {
        /*
        let requestProcessor = ElectrodeRequestProcessor<String, String, Any>(requestName: kRequestGetUserName,
                                                                            requestPayload: nil,
                                                                            respClass: String.self,
                                                                            responseItemType: nil,
                                                                            responseListener: responseListner)
 */
    }

}
