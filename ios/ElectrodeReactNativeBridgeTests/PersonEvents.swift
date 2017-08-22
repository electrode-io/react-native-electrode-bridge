//
//  PersonEvent.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class PersonEvents:  PersonAPI.Events {
    override public func addPersonAddedEventListenr(eventListener: @escaping ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: PersonAPI.kEventPersonAdded, eventPayloadClass: Person.self, eventListener: eventListener)
        listenerProcessor.execute()
    }
    
    override public func addPersonNameUpdatedEventListener(eventListener: @escaping ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: PersonAPI.kEventPersonNameUpdated, eventPayloadClass: String.self, eventListener: eventListener)
        listenerProcessor.execute()
    }
    
    override public func emitEventPersonAdded(person: Person) {
        let eventProcessor = EventProcessor(eventName: PersonAPI.kEventPersonAdded, eventPayload: person)
        eventProcessor.execute()
        
    }
    override public func emitEventPersonNameUpdated(updatedName: String) {
        let eventProcessor = EventProcessor(eventName: PersonAPI.kEventPersonNameUpdated, eventPayload: updatedName)
        eventProcessor.execute()
    }
}
