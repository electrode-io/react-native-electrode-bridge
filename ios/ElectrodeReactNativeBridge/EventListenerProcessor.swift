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
    private let logger = ElectrodeConsoleLogger.sharedInstance()
    
    public init(eventName: String, eventPayloadClass: T.Type, eventListener: @escaping ElectrodeBridgeEventListener) {
        self.tag = String(describing: type(of: self))
        self.eventName = eventName
        self.eventPayloadClass = eventPayloadClass
        self.appEventListener = eventListener
        super.init()
    }
    
    public func execute() {
        ElectrodeBridgeHolder.addEventListner(withName: eventName, eventListner: { (eventPayload: Any?) in
            self.logger.debug("Processing final result for the event with payload bundle (\(String(describing: eventPayload)))")
            let result = try? NSObject.generateObject(data: eventPayload as AnyObject, classType: self.eventPayloadClass)
            self.appEventListener(result)
        })
    }
}
