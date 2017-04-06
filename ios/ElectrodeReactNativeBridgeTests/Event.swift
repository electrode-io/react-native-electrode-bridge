//
//  Event.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import Foundation

let kEventPersonAdded = "kEventPersonAdded"
let kEventPersonNameUpdated = "kEventPersonNameUpdated"

@testable import ElectrodeReactNativeBridge


@objc public protocol Event {
    func addPersonAddedEventListenr(eventListener: ElectrodeBridgeEventListener)
    func addPersonNameUpdatedEventListener(eventListener: ElectrodeBridgeEventListener)
    func emitEventPersonAdded(person: Person)
    func emitEventPersonNameUpdated(updatedName: String)
}


