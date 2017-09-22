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

public class BirthYear: ElectrodeObject, Bridgeable {
    let month: Int
    let year: Int

    public init(month: Int, year: Int) {
        self.month = month
        self.year = year
        super.init()
    }

    public override init() {
        month = 0
        year = 0
        super.init()
    }

    public required init(dictionary: [AnyHashable: Any]) {
        if let month = dictionary["month"] as? Int,
            let year = dictionary["year"] as? Int {
            self.month = month
            self.year = year
        } else {
            assertionFailure("year property is required")
            month = dictionary["month"] as! Int
            year = dictionary["year"] as! Int
        }
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {
        return ["month": self.month, "year": self.year] as NSDictionary
    }
}
