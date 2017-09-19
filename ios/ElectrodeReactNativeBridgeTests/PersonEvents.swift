//
//  PersonEvent.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class PersonEvents: PersonAPI.Events {
    public override func addPersonAddedEventListenr(eventListener: @escaping ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: PersonAPI.kEventPersonAdded, eventPayloadClass: Person.self, eventListener: eventListener)
        listenerProcessor.execute()
    }

    public override func addPersonNameUpdatedEventListener(eventListener: @escaping ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: PersonAPI.kEventPersonNameUpdated, eventPayloadClass: String.self, eventListener: eventListener)
        listenerProcessor.execute()
    }

    public override func emitEventPersonAdded(person: Person) {
        let eventProcessor = EventProcessor(eventName: PersonAPI.kEventPersonAdded, eventPayload: person)
        eventProcessor.execute()
    }

    public override func emitEventPersonNameUpdated(updatedName: String) {
        let eventProcessor = EventProcessor(eventName: PersonAPI.kEventPersonNameUpdated, eventPayload: updatedName)
        eventProcessor.execute()
    }
}
