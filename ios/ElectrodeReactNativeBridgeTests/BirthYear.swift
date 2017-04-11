//
//  BirthYear.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class BirthYear: ElectrodeObject, Bridgeable {
    let month: Int
    let year: Int
    
    public init(month: Int, year: Int) {
        self.month = month
        self.year = year
        super.init()
    }
    
    required public init(dictionary: [AnyHashable: Any]) {
        if let month = dictionary["month"] as? Int,
            let year = dictionary["year"] as? Int{
            self.month = month
            self.year = year
        }else {
            assertionFailure("year property is required")
            self.month = dictionary["month"] as! Int
            self.year = dictionary["year"] as! Int
        }
        super.init(dictionary: dictionary)
    }
    

    public func toDictionary() -> NSDictionary {
        return ["month": self.month, "year": self.year] as NSDictionary
        
    }
}
