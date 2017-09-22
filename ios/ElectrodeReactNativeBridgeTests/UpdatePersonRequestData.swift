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

public class UpdatePersonRequestData: ElectrodeObject, Bridgeable {
    let firstName: String
    let lastName: String
    let status: Status?

    public init(firstName: String, lastName: String, status: Status?) {
        self.firstName = firstName
        self.lastName = lastName
        self.status = status
        super.init()
    }

    public required init(dictionary: [AnyHashable: Any]) {
        if let firstName = dictionary["firstName"] as? String,
            let lastName = dictionary["lastName"] as? String {
            self.firstName = firstName
            self.lastName = lastName
        } else {
            assertionFailure("Missing required params in Object Status need member property")
            firstName = dictionary["firstName"] as! String
            lastName = dictionary["lastName"] as! String
        }

        if let statusDict = dictionary["status"] as? [AnyHashable: Any] {
            status = Status(dictionary: statusDict)
        } else {
            status = nil
        }
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {
        var dict = [AnyHashable: Any]()
        dict["firstName"] = firstName
        dict["lastName"] = lastName

        if let validStatus = self.status {
            dict["status"] = validStatus.toDictionary()
        }

        return dict as NSDictionary
    }
}
