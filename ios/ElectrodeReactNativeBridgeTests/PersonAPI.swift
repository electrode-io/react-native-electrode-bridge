//
//  PersonAPI.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import Foundation

@objc public protocol PersonAPI {
    var event: Event { get }
}

class APersonAPI: PersonAPI {
    var event: Event {return PersonEvent()}
}

