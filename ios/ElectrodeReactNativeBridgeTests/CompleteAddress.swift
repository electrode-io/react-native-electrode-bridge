//
//  Address.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 7/17/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class CompleteAddress: ElectrodeObject, Bridgeable {
    let streetOne: String
    let streetTwo: String?
    let zipcode: String
    let state: String
    let city: String
    public init(streetOne: String, streetTwo: String?, zipcode: String, state: String, city: String) {
        self.streetOne = streetOne
        self.streetTwo = streetTwo
        self.zipcode = zipcode
        self.state = state
        self.city = city
        super.init()
    }

    public required init(dictionary: [AnyHashable: Any]) {
        if let validStreetOne = dictionary["streetOne"] as? String,
            let validZipcode = dictionary["zipcode"] as? String,
            let validState = dictionary["state"] as? String,
            let validCity = dictionary["city"] as? String {
            streetOne = validStreetOne
            zipcode = validZipcode
            state = validState
            city = validCity
        } else {
            assertionFailure("Invalid Address dictionary")
            streetOne = dictionary["streetOne"] as! String
            zipcode = dictionary["zipcode"] as! String
            state = dictionary["state"] as! String
            city = dictionary["city"] as! String
        }

        streetTwo = dictionary["streetTwo"] as? String
        super.init()
    }

    public func toDictionary() -> NSDictionary {
        var dict = ["streetOne": self.streetOne, "zipcode": self.zipcode, "state": self.state, "city": self.city]

        if let nonNullStreetTwo = self.streetTwo {
            dict["streetTwo"] = nonNullStreetTwo
        }

        return dict as NSDictionary
    }
}
