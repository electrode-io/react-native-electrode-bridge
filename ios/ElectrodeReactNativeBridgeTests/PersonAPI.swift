//
//  PersonAPI.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import Foundation



public class PersonAPI {
    static let kEventPersonAdded = "kEventPersonAdded"
    static let kEventPersonNameUpdated = "kEventPersonNameUpdated"
    static let kRequestGetPerson = "kRequestGetPerson"
    static let kRequestGetStatus = "kRequestGetStatus"
    static let kRequestGetUserName = "kRequestGetUserName"
    static let kRequestGetAge = "kRequestGetAge"
    lazy var event: Events = { return PersonEvents()}()
    lazy var request: Requests = {return PersonRequests()}()
    
    public class Events {
        func addPersonAddedEventListenr(eventListener: ElectrodeBridgeEventListener) {
            assertionFailure("need override")
        }
        func addPersonNameUpdatedEventListener(eventListener: ElectrodeBridgeEventListener) {
            assertionFailure("need override")
        }
        func emitEventPersonAdded(person: Person) {
            assertionFailure("need override")

        }
        func emitEventPersonNameUpdated(updatedName: String) {
            
        }
    }
    
    public class Requests {
        /***
         * Registers a handler that returns the current user when {@link #getPerson(ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        func registerGetPersonRequestHandler(handler: ElectrodeBridgeRequestHandler) {
            assertionFailure("need override")

        }
        /**
         * Registers a handler  that returns the user status when {@link #getStatus(Person, ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        func registerGetStatusRequestHandler(handler: ElectrodeBridgeRequestHandler) {
            assertionFailure("need override")

        }
        func registerGetAgeRequestHandler(handler: ElectrodeBridgeRequestHandler) {
            assertionFailure("need override")
        }
        
        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Person>#onSuccess({@link Person})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
         *
         * @param response {@link ElectrodeBridgeResponseListener<Person>} Request listener as a call back to be called once the operation is completed.
         */
        func getPerson(responseListner: ElectrodeBridgeResponseListener) {
            assertionFailure("need override")

        }
        
        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Status>#onSuccess({@link Status})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
         *
         * @param response {@link ElectrodeBridgeResponseListener<Status>} Request listener as a call back to be called once the operation is completed.
         */
        func getStatus(person: Person, responseListener: ElectrodeBridgeResponseListener) {
            assertionFailure("need override")

        }
        
        func getUserName(responseListner: ElectrodeBridgeResponseListener) {
            assertionFailure("need override")

        }
        
        func getAge(name: String, responseListener: ElectrodeBridgeResponseListener) {
            assertionFailure("need override")

        }
        
    }
}


