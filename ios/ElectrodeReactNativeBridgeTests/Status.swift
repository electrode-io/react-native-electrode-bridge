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

public class Status: ElectrodeObject, Bridgeable {
    let log: Bool
    let member: Bool
    private static let tag = String(describing: type(of: self))

    public init(log: Bool, member: Bool) {
        self.log = log
        self.member = member
        super.init()
    }

    public required init(dictionary: [AnyHashable: Any]) {
        if let log = dictionary["log"] as? Bool,
            let member = dictionary["member"] as? Bool {
            self.log = log
            self.member = member
        } else {
            assertionFailure("Missing required params in Object Status need member property")
            log = dictionary["log"] as! Bool
            member = dictionary["member"] as! Bool
        }
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {
        return ["log": self.log, "member": self.member] as NSDictionary
    }
}
