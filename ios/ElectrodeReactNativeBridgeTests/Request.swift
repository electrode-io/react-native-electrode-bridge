//
//  Request.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

@testable import ElectrodeReactNativeBridge

public let kRequestGetPerson = "kRequestGetPerson"
public let kRequestGetStatus = "kRequestGetStatus"
public let kRequestGetUserName = "kRequestGetUserName"
public let kRequestGetAge = "kRequestGetAge"

@objc public protocol Request {
    /***
     * Registers a handler that returns the current user when {@link #getPerson(ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
     *
     * @param handler {@link ElectrodeBridgeRequestHandler}
     */
    func registerGetPersonRequestHandler(handler: ElectrodeBridgeRequestHandler)
    /**
     * Registers a handler  that returns the user status when {@link #getStatus(Person, ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
     *
     * @param handler {@link ElectrodeBridgeRequestHandler}
     */
    func registerGetStatusRequestHandler(handler: ElectrodeBridgeRequestHandler)
    func registerGetAgeRequestHandler(handler: ElectrodeBridgeRequestHandler)
    
    /**
     * Calling this method will trigger a bridge request to call the registered handler for a response.
     * <p>
     * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Person>#onSuccess({@link Person})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
     *
     * @param response {@link ElectrodeBridgeResponseListener<Person>} Request listener as a call back to be called once the operation is completed.
     */
    func getPerson(responseListner: ElectrodeBridgeResponseListener)
    
    /**
     * Calling this method will trigger a bridge request to call the registered handler for a response.
     * <p>
     * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Status>#onSuccess({@link Status})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
     *
     * @param response {@link ElectrodeBridgeResponseListener<Status>} Request listener as a call back to be called once the operation is completed.
     */
    func getStatus(person: Person, responseListener: ElectrodeBridgeResponseListener)
    
    func getUserName(responseListner: ElectrodeBridgeResponseListener)
    
    func getAge(name: String, responseListener: ElectrodeBridgeResponseListener)
    
}
