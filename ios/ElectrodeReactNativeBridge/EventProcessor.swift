//
//  ElectrodeEventProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/2/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

public class EventProcessor<T>: NSObject, Processor {
    private let tag: String
    private let eventPayload: T?
    private let eventName: String
    
    public init(eventName: String, eventPayload: T?) {
        self.tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayload = eventPayload
        super.init()
    }
    
    public func execute() {
        print("\(self.tag) EventProcessor is emitting event (\(eventName)) with payload (\(eventPayload))")
        let event = ElectrodeBridgeEvent(name: eventName, type: .event, data: eventPayload)
        ElectrodeBridgeHolder.send(event)
    }
}
