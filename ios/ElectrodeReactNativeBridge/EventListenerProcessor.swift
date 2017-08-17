//
//  EventListenerProcessor.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/2/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class EventListenerProcessor<T>: NSObject, Processor {
    private let tag: String
    private let eventName: String
    private let eventPayloadClass: T.Type
    private let appEventListener: ElectrodeBridgeEventListener
    
    public init(eventName: String, eventPayloadClass: T.Type, eventListener: ElectrodeBridgeEventListener) {
        self.tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayloadClass = eventPayloadClass
        self.appEventListener = eventListener
        super.init()
    }
    
    public func execute() {
        let intermediateListener = ElectrodeEventListenerImplementor(eventPayloadClass: eventPayloadClass, onEventClosure: {
            (any: Any?) in
            self.appEventListener.onEvent(any)
        })
        ElectrodeBridgeHolder.addEventListner(withName: eventName, eventListner: intermediateListener)
    }
}

private class ElectrodeEventListenerImplementor<T>: NSObject, ElectrodeBridgeEventListener {
    private let logger = ElectrodeConsoleLogger.sharedInstance()
    private let eventPayloadClass: T.Type
    private let onEventClosure: (Any?) -> ()
    
    init(eventPayloadClass: T.Type, onEventClosure: @escaping (Any?) -> ()) {
        self.eventPayloadClass = eventPayloadClass
        self.onEventClosure = onEventClosure
    }
    
    func onEvent(_ eventPayload: Any?) {
        logger.debug("Processing final result for the event with payload bundle (\(eventPayload))")
        let result = try? NSObject.generateObject(data: eventPayload as AnyObject, classType: eventPayloadClass)
        onEventClosure(result)
    }
}
