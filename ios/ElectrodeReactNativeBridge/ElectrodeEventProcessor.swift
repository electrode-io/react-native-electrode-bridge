//
//  ElectrodeEventProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/2/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

class EventProcessor<T: Bridgeable>: NSObject {
    private let tag: String
    private let eventPayload: T?
    private let eventName: String
    
    init(eventName: String, eventPayload: T?) {
        self.tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayload = eventPayload
    }
    
    func execute() {
        print("EventProcessor is emitting event (\(eventName)) with payload (\(eventPayload))")
        guard let eventData = eventPayload?.toDictionary() as? [AnyHashable : Any] else {
            assertionFailure("\(tag): attempting to type case to [AnyHashable: Any] failed. Please check your payload")
            return
        }
        let event = ElectrodeBridgeEventNew.createEvent(withData: eventData)
        guard let validEvent = event else {
            assertionFailure("\(tag): Failed to create a valid ElectrodeBridgeEvent")
            return
        }
        ElectrodeBridgeHolderNew.sharedInstance().sendEvent(validEvent)
    }
}
