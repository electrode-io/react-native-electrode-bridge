//
//  PersonEvent.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class PersonEvent: NSObject, Event {
    public func addPersonAddedEventListenr(eventListener: ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: kEventPersonAdded, eventPayloadClass: Person.self, eventListener: eventListener)
        listenerProcessor.execute()
    }
    
    public func addPersonNameUpdatedEventListener(eventListener: ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: kEventPersonNameUpdated, eventPayloadClass: String.self, eventListener: eventListener)
        listenerProcessor.execute()
    }
    
    public func emitEventPersonAdded(person: Person) {
        let eventProcessor = EventProcessor(eventName: kEventPersonAdded, eventPayload: person)
        eventProcessor.execute()
        
    }
    public func emitEventPersonNameUpdated(updatedName: String) {
        let eventProcessor = EventProcessor(eventName: kEventPersonNameUpdated, eventPayload: updatedName)
        eventProcessor.execute()
    }
}
