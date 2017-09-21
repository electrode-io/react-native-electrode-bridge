/*
 * Copyright 2017 WalmartLabs
 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
