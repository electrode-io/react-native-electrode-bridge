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
        tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayload = eventPayload
    }
    
    func execute() {
        print("EventProcessor is emitting event (\(eventName)) with payload (\(eventPayload))")
        
        // Convert the eventPayload
        let eventDictionary = eventPayload?.toDictionary()
        
        // Build the event
        let event = ElectrodeBridgeEvent(name: eventName, data: eventDictionary as! [AnyHashable : Any]?, mode: EBDispatchMode.native)
        
        // Send the event off!
        ElectrodeBridgeHolder.sharedInstance().emitEvent(event)
    }
}

