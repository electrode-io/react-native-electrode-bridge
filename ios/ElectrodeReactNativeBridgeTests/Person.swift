//
//  Person.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit


class Person: NSObject, Bridgeable {
    private static let tag = String(describing: type(of: self))
    
    let name: String
    let age: Int
    let month: Int
    let status: Status
    let position: Position
    let birthYear: BirthYear
    
    init(name: String, age: Int, month: Int, status: Status, position: Position, birthYear: BirthYear) {
        self.name = name
        self.age = age
        self.month = month
        self.status = status
        self.position = position
        self.birthYear = birthYear
        super.init()
    }
    
    
    convenience init?(dictionary: [String: Any]) {
        guard let name = dictionary["name"] as? String else {
            assertionFailure("\(Person.tag) need name property")
            return nil
        }
        
        guard let age = dictionary["age"] as? Int else {
            assertionFailure("\(Person.tag) need age property")
            return nil
        }
        
        guard let month = dictionary["month"] as? Int else {
            assertionFailure("\(Person.tag) need month property")
            return nil
        }
        
        guard let statusDict = dictionary["month"] as? [String: Any] else {
            assertionFailure("\(Person.tag) need status property")
            return nil
        }
        
        guard let statusObj = Status(dictionary: statusDict) else {
            assertionFailure("\(Person.tag) need status property")
            return nil
        }
        
        guard let positionDict = dictionary["position"] as? [String: Any] else {
            assertionFailure("\(Person.tag) need position property")
            return nil
        }
        
        guard let positionObj = Position(dictionary: positionDict) else {
            assertionFailure("\(Person.tag) need position property")
            return nil
        }
        
        guard let birthYearDict = dictionary["birthYear"] as? [String: Any] else {
            assertionFailure("\(Person.tag) need birthYear property")
            return nil
        }
        
        guard let birthYearObj = BirthYear(dictionary: birthYearDict) else {
            assertionFailure("\(Person.tag) need birthYear property")
            return nil
        }
        
        self.init(name: name,
                  age: age,
                  month: month,
                  status: statusObj,
                  position: positionObj,
                  birthYear: birthYearObj)
        
    }
    
    convenience init?(data: Data?) {
        guard let validData = data else{
            debugPrint(Person.tag)
            return nil
        }
        do {
            let parsedData = try JSONSerialization.jsonObject(with: validData, options: .allowFragments)
            guard let dict = parsedData as? [String: Any] else {
                assertionFailure("\(Person.tag) cannot deserialize")
                return nil
            }
            
            self.init(dictionary: dict)
        } catch let error as NSError {
            debugPrint("\(Person.tag): \(error)")
            return nil
        } catch let error {
            debugPrint("\(Person.tag): \(error)")
            return nil
        }
    }

    func toDictionary() -> NSDictionary {
        return [
            "name": self.name,
            "age": self.age,
            "month": self.month,
            "status": self.status.toDictionary(),
            "position": self.position.toDictionary(),
            "birthYear": self.birthYear.toDictionary()
                ] as NSDictionary
    }
}
