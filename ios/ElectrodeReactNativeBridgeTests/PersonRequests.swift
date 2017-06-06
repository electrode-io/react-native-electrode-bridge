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
    
    override func registerUpdatePersonRequestHandler(handler: ElectrodeBridgeRequestHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestPostPersonUpdate, reqClass: UpdatePersonRequestData.self, respClass: Person.self, requestHandler: handler)
        requestHandler.execute()
    }
    
    override func registerFindPersonsByStatus(handler: ElectrodeBridgeRequestHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestFindPersonsByStatus, reqClass: Status.self, respClass: Person.self, requestHandler: handler)
        requestHandler.execute()
    }
    
    override func registerFindPersonsAgeByName(handler: ElectrodeBridgeRequestHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestFindPersonsByName, reqClass: String.self, respClass: Int.self, requestHandler: handler)
        requestHandler.execute()
    }
    
    override func getPerson(success: @escaping ElectrodeBridgeResponseListenerSuccessBlock, failure: @escaping ElectrodeBridgeResponseListenerFailureBlock)
    {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Person, Any>(requestName: PersonAPI.kRequestGetPerson,
                                                          requestPayload: nil,
                                                          respClass: Person.self,
                                                          responseItemType: nil,
                                                          success: success,
                                                          failure: failure)
        requestProcessor.execute()
    }
    
    override func getStatus(person: Person,
                            success: @escaping ElectrodeBridgeResponseListenerSuccessBlock,
                            failure: @escaping ElectrodeBridgeResponseListenerFailureBlock) {
        let requestProcessor = ElectrodeRequestProcessor<Person, Status, Any> (requestName: PersonAPI.kRequestGetStatus, requestPayload:person,respClass: Status.self,responseItemType: nil,success: success,failure: failure)
        
        
        requestProcessor.execute()
    }
    
    override func getUserName(success: @escaping ElectrodeBridgeResponseListenerSuccessBlock, failure: @escaping ElectrodeBridgeResponseListenerFailureBlock) {
        let requestProcessor = ElectrodeRequestProcessor<None, String, Any>(requestName: PersonAPI.kRequestGetUserName,
                                                                              requestPayload: nil,
                                                                              respClass: String.self,
                                                                              responseItemType: nil,
                                                                              success: success,
                                                                              failure: failure)
        requestProcessor.execute()

    }
    
    override func getAge(name: String, success: @escaping ElectrodeBridgeResponseListenerSuccessBlock, failure: @escaping ElectrodeBridgeResponseListenerFailureBlock) {
        let requestProcessor = ElectrodeRequestProcessor<None, String, Any>(requestName: PersonAPI.kRequestGetAge,
                                                                             requestPayload: name,
                                                                             respClass: String.self,
                                                                             responseItemType: nil,
                                                                             success: success,
                                                                             failure: failure)
        requestProcessor.execute()
 
    }
    
    override func updatePersonPost(updatePersonRequestData: UpdatePersonRequestData,
                                   success: @escaping ElectrodeBridgeResponseListenerSuccessBlock,
                                   failure: @escaping ElectrodeBridgeResponseListenerFailureBlock) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Person, Any>(requestName: PersonAPI.kRequestPostPersonUpdate,
                                                                            requestPayload: updatePersonRequestData,
                                                                            respClass: Person.self,
                                                                            responseItemType: nil,
                                                                            success: success,
                                                                            failure: failure)
        requestProcessor.execute()
    }
    
    
    override func findPersonsByStatus(statusList: [Status],
                                      success: @escaping ElectrodeBridgeResponseListenerSuccessBlock,
                                      failure: @escaping ElectrodeBridgeResponseListenerFailureBlock) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Array<Any>, Any>(requestName: PersonAPI.kRequestFindPersonsByStatus,
                                                                            requestPayload: statusList,
                                                                            respClass: Array<Any>.self,
                                                                            responseItemType: Person.self,
                                                                            success: success,
                                                                            failure: failure)
        requestProcessor.execute()
    }
    
    
    override func findPersonByAge(names: [String],
                                  success: @escaping ElectrodeBridgeResponseListenerSuccessBlock,
                                  failure: @escaping ElectrodeBridgeResponseListenerFailureBlock) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Array<Any>, Any>(
            requestName: PersonAPI.kRequestFindPersonsByName,
            requestPayload: names,
            respClass: Array<Any>.self,
            responseItemType: Int.self,
            success: success,
            failure: failure)
        requestProcessor.execute()
    }

}
