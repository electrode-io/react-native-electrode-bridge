//
//  Bridgeable.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/3/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import Foundation

// @objc requires this protocol to be a class protocol
// mark public because ElectrodeBridgeMessage is public
@objc public protocol Bridgeable {
    func toDictionary() -> NSDictionary
}
