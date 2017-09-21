/*
 * Copyright 2017 WalmartLabs
 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import UIKit

class PersonRequests: PersonAPI.Requests {
    override func registerGetPersonRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) {
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestGetPerson, reqClass: None.self, respClass: Person.self, requestCompletionHandler: handler)
        requestHandlerProcessor.execute()
    }

    override func registerGetStatusRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) {
        let requestHandlerProcessor = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestGetStatus, reqClass: Person.self, respClass: Status.self, requestCompletionHandler: handler)
        requestHandlerProcessor.execute()
    }

    override func registerGetAgeRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestGetAge, reqClass: String.self, respClass: Int.self, requestCompletionHandler: handler)
        requestHandler.execute()
    }

    override func registerUpdatePersonRequestHandler(handler: @escaping ElectrodeBridgeRequestCompletionHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestPostPersonUpdate, reqClass: UpdatePersonRequestData.self, respClass: Person.self, requestCompletionHandler: handler)
        requestHandler.execute()
    }

    override func registerFindPersonsByStatus(handler: @escaping ElectrodeBridgeRequestCompletionHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestFindPersonsByStatus, reqClass: Array<Any>.self, reqItemType: Status.self, respClass: Person.self, requestCompletionHandler: handler)
        requestHandler.execute()
    }

    override func registerFindPersonsAgeByName(handler: @escaping ElectrodeBridgeRequestCompletionHandler) {
        let requestHandler = ElectrodeRequestHandlerProcessor(requestName: PersonAPI.kRequestFindPersonsByName, reqClass: String.self, respClass: Int.self, requestCompletionHandler: handler)
        requestHandler.execute()
    }

    override func getPerson(responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Person, Any>(requestName: PersonAPI.kRequestGetPerson,
                                                                                  requestPayload: nil,
                                                                                  respClass: Person.self,
                                                                                  responseItemType: nil,
                                                                                  responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }

    override func getStatus(person: Person,
                            responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<Person, Status, Any>(requestName: PersonAPI.kRequestGetStatus,
                                                                              requestPayload: person,
                                                                              respClass: Status.self,
                                                                              responseItemType: nil,
                                                                              responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }

    override func getUserName(responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<None, String, Any>(requestName: PersonAPI.kRequestGetUserName,
                                                                            requestPayload: nil,
                                                                            respClass: String.self,
                                                                            responseItemType: nil,
                                                                            responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }

    override func getAge(name: String, responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<None, String, Any>(requestName: PersonAPI.kRequestGetAge,
                                                                            requestPayload: name,
                                                                            respClass: String.self,
                                                                            responseItemType: nil,
                                                                            responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }

    override func updatePersonPost(updatePersonRequestData: UpdatePersonRequestData,
                                   responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Person, Any>(requestName: PersonAPI.kRequestPostPersonUpdate,
                                                                                  requestPayload: updatePersonRequestData,
                                                                                  respClass: Person.self,
                                                                                  responseItemType: nil,
                                                                                  responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }

    override func findPersonsByStatus(statusList: [Status],
                                      responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Array<Any>, Any>(requestName: PersonAPI.kRequestFindPersonsByStatus,
                                                                                      requestPayload: statusList,
                                                                                      respClass: Array<Any>.self,
                                                                                      responseItemType: Person.self,
                                                                                      responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }

    override func findPersonByAge(names: [String],
                                  responseCompletionHandler: @escaping ElectrodeBridgeResponseCompletionHandler) {
        let requestProcessor = ElectrodeRequestProcessor<Bridgeable, Array<Any>, Any>(requestName: PersonAPI.kRequestFindPersonsByName,
                                                                                      requestPayload: names,
                                                                                      respClass: Array<Any>.self,
                                                                                      responseItemType: Int.self,
                                                                                      responseCompletionHandler: responseCompletionHandler)
        requestProcessor.execute()
    }
}
