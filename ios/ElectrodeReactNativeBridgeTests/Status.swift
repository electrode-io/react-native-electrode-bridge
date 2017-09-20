//
//  Status.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

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
