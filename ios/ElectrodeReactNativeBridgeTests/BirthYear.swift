//
//  BirthYear.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class BirthYear: NSObject, Bridgeable {
    let month: Int
    let year: Int
    
    convenience init?(data: Data?) {
        if let data = data {
            do {
                let parsedData: Any = try JSONSerialization.jsonObject(with: data, options: .allowFragments)
                guard let dict = parsedData as? [String: Any] else {
                    debugPrint("BirthYear cannot convert to object")
                    return nil
                }
                
                self.init(dictionary: dict)
            } catch let error as NSError {
                debugPrint(error)
                return nil
            } catch let error{
                debugPrint(error)
                return nil
            }
        } else {
            return nil
        }
    }
    
    convenience init?(dictionary: [String: Any]) {
        guard let month = dictionary["month"] as? Int else {
            assertionFailure("month property is required")
            return nil
        }
        guard let year = dictionary["year"] as? Int else {
            assertionFailure("year property is required")
            return nil
        }
        
        self.init(month: month, year: year)
    }
    
    init(month: Int, year: Int) {
        self.month = month
        self.year = year
        super.init()
    }
    
    func toDictionary() -> NSDictionary {
        return ["month": self.month, "year": self.year] as NSDictionary
        
    }
}
