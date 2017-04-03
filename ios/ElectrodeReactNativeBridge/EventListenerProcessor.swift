//
//  EventListenerProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/2/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class EventListenerProcessor<T: Bridgeable>: NSObject {
    private let tag: String
    private let eventName: String
    private let eventPayloadClass: T.Type
    private let appEventListener: ElectrodeBridgeEventListener
    
    init(eventName: String, eventPayloadClass: T.Type, eventListener: ElectrodeBridgeEventListener) {
        self.tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayloadClass = eventPayloadClass
        self.appEventListener = eventListener
        super.init()
    }
    
    func execute() {
    }
}

private class ElectrodeEventListenerImplementor<T: Bridgeable>: NSObject, ElectrodeBridgeEventListener {
    
    private let appEventListener: ElectrodeBridgeEventListener
    private let eventPayloadClass: T.Type
    
    init(eventPayloadClass: T.Type, appEventListener: ElectrodeBridgeEventListener) {
        self.eventPayloadClass = eventPayloadClass
        self.appEventListener = appEventListener
    }
    
    func onEvent(_ eventPayload: Any?) {
        print("Processing final result for the event with payload bundle (\(eventPayload))")
        let result = NSObject.generateObject(data: eventPayload as! [AnyHashable : Any], passedClass: eventPayloadClass)
        appEventListener.onEvent(result)
    }
}
