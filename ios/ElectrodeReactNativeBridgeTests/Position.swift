//
//  Position.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class Position: NSObject, Bridgeable{
    let lat: Double
    let lng: Double
    
    init(lat: Double, lng: Double) {
        self.lat = lat
        self.lng = lng
        super.init()
    }
    
    convenience init?(dictionary: [String: Any]) {
        guard let lat = dictionary["lat"] as? Double else {
            assertionFailure("Position need lat property")
            return nil
        }
        
        guard let lng = dictionary["lng"] as? Double else {
            assertionFailure("Position need lng property")
            return nil
        }
        
        self.init(lat: lat, lng: lng)
    }
    
    convenience init?(data: Data?) {
        do {
            guard let validData = data else {
                return nil
            }
            let parsedData: Any = try JSONSerialization.jsonObject(with: validData, options: .allowFragments)

            guard let dict = parsedData as? [String: Any] else {
                debugPrint("Position could not deserialize")
                return nil
            }
            
            self.init(dictionary: dict)
            
        } catch let error as NSError {
            debugPrint("Position: \(error)")
            return nil
        } catch let error {
            debugPrint("Position \(error)")
            return nil
        }
    }
    
    func toDictionary() -> NSDictionary {
        return ["lat": self.lat, "lgn": self.lng] as NSDictionary
    }
    
}
