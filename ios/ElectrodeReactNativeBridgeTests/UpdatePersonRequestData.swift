//
//  UpdatePersonRequestData.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/11/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

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
    
    required public init(dictionary: [AnyHashable: Any]) {
        if let firstName = dictionary["firstName"] as? String,
            let lastName = dictionary["lastName"] as? String {
            self.firstName = firstName
            self.lastName = lastName
        } else {
            assertionFailure("Missing required params in Object Status need member property")
            self.firstName = dictionary["firstName"] as! String
            self.lastName = dictionary["lastName"] as! String
        }
        
        if let statusDict = dictionary["status"] as? [AnyHashable: Any]{
            self.status = Status(dictionary: statusDict)
        } else {
            self.status = nil
        }
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {
        var dict = [AnyHashable: Any]()
        dict["firstName"] = self.firstName
        dict["lastName"] = self.lastName
        
        if let validStatus = self.status {
            dict["status"] = validStatus.toDictionary()
        }
        
        return dict as NSDictionary
    }
}
