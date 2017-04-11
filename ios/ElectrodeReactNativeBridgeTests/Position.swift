//
//  Position.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class Position: ElectrodeObject, Bridgeable{
    let lat: Double
    let lng: Double
    
    public init(lat: Double, lng: Double) {
        self.lat = lat
        self.lng = lng
        super.init()
    }
    
    required public init(dictionary: [AnyHashable: Any]) {
        if let lat = dictionary["lat"] as? Double,
            let lng = dictionary["lng"] as? Double {
            self.lat = lat
            self.lng = lng
        } else {
            assertionFailure("Position need lng property")
            self.lat = dictionary["lat"] as! Double
            self.lng = dictionary["lng"] as! Double
        }

        super.init(dictionary: dictionary)
    }
        
    public func toDictionary() -> NSDictionary {
        return ["lat": self.lat, "lgn": self.lng] as NSDictionary
    }
    
}
