//
//  PersonRequest.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class PersonRequests: PersonAPI.Requests {
    override func registerGetPersonRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestGetPerson, reqClass: None.self, respClass: Person.self, requestHandler: handler)
        requestHandlerProcessor.execute()
    }
    
    override func registerGetStatusRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestGetStatus, reqClass: Person.self, respClass: Status.self, requestHandler: handler)
        requestHandlerProcessor.execute()
    }
    override func registerGetAgeRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestGetAge, reqClass: String.self, respClass: Int.self, requestHandler: handler)
        requestHandler.execute()
    }
    
    override func getPerson(responseListner responseListener: ElectrodeBridgeResponseListener) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Person, Any>(requestName: PersonAPI.kRequestGetPerson,
                                                          requestPayload: nil,
                                                          respClass: Person.self,
                                                          responseItemType: nil,
                                                          responseListener: responseListener)
        requestProcessor.execute()
    }
    
    override func getStatus(person: Person, responseListener: ElectrodeBridgeResponseListener) {
        let requestProcessor = ElectrodeRequestProcessor<Person, Status, Any>(requestName: PersonAPI.kRequestGetStatus,                                                                                requestPayload: person, respClass: Status.self, responseItemType: nil, responseListener: responseListener)
        requestProcessor.execute()
    }
    
    override func getUserName(responseListner: ElectrodeBridgeResponseListener) {
        let requestProcessor = ElectrodeRequestProcessor<None, String, Any>(requestName: PersonAPI.kRequestGetUserName,
                                                                              requestPayload: nil,
                                                                              respClass: String.self,
                                                                              responseItemType: nil,
                                                                              responseListener: responseListner)
        requestProcessor.execute()

    }
    
    override func getAge(name: String, responseListener: ElectrodeBridgeResponseListener) {
        /*
        let requestProcessor = ElectrodeRequestProcessor<String, String, Any>(requestName: kRequestGetUserName,
                                                                            requestPayload: nil,
                                                                            respClass: String.self,
                                                                            responseItemType: nil,
                                                                            responseListener: responseListner)
 */
    }

}
