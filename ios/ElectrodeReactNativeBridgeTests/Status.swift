//
//  Status.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class Status: NSObject, Bridgeable{
    let log: Bool
    let member: Bool
    private static let tag = String(describing: type(of:self))
    
    init(log: Bool, member: Bool) {
        self.log = log
        self.member = member
        super.init()
    }
    
    convenience init?(dictionary: [String: Any]) {
        guard let log = dictionary["log"] as? Bool else {
            assertionFailure("\(Status.tag) need log property")
            return nil
        }
        
        guard let member = dictionary["member"] as? Bool else {
            assertionFailure("\(Status.tag) need member property")
            return nil
        }
        
        self.init(log: log, member: member)
    }
    
    convenience init?(data: Data?) {
        guard let validData = data else {
            debugPrint("\(Status.tag): empty data")
            return nil
        }
        
        do {
            let parsedData = try JSONSerialization.jsonObject(with: validData, options: .allowFragments)
            guard let dict = parsedData as? [String: Any] else {
                assertionFailure("\(Status.tag) : failed to parse")
                return nil
            }
            
            self.init(dictionary: dict)
        } catch let error as NSError {
            debugPrint("\(Status.tag): \(error)")
            return nil
        } catch let error {
            debugPrint(error)
            return nil
        }
    }
    
    func toDictionary() -> NSDictionary {
        return ["log": self.log, "member":self.member] as NSDictionary
    }
}
