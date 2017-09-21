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

public class Position: ElectrodeObject, Bridgeable {
    let lat: Double
    let lng: Double

    public init(lat: Double, lng: Double) {
        self.lat = lat
        self.lng = lng
        super.init()
    }

    public override init() {
        lat = 0
        lng = 0
        super.init()
    }

    public required init(dictionary: [AnyHashable: Any]) {
        if let lat = dictionary["lat"] as? Double,
            let lng = dictionary["lng"] as? Double {
            self.lat = lat
            self.lng = lng
        } else {
            assertionFailure("Position need lng property")
            lat = dictionary["lat"] as! Double
            lng = dictionary["lng"] as! Double
        }

        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {
        return ["lat": self.lat, "lgn": self.lng] as NSDictionary
    }
}
