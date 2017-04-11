//
//  Person.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//


public class Person: ElectrodeObject, Bridgeable {
    private static let tag = String(describing: type(of: self))
    
    var name: String
    var age: Int?
    var hiredMonth: Int
    var status: Status?
    var position: Position?
    var birthYear: BirthYear?
    
    public init(name: String, age: Int?, hiredMonth: Int, status: Status?, position: Position?, birthYear: BirthYear?) {
        self.name = name
        self.age = age
        self.hiredMonth = hiredMonth
        self.status = status
        self.position = position
        self.birthYear = birthYear
        super.init()
    }
    


    required public init(dictionary: [AnyHashable: Any]) {
        if let name = dictionary["name"] as? String,
            let hiredMonth = dictionary["hiredMonth"] as? Int {
            self.name = name
            self.hiredMonth = hiredMonth
        } else {
            assertionFailure("\(Person.tag) need month property")
            self.hiredMonth = dictionary["month"] as! Int
            self.name = dictionary["name"] as! String
        }
        
        //optional params
        let age = dictionary["age"] as? Int?
        
        let statusObj: Status?
        if let statusDict = dictionary["month"] as? [AnyHashable: Any]
        {
            statusObj = Status(dictionary: statusDict)
        } else {
            statusObj = nil
        }
        self.status = statusObj
        
        let positionObj: Position?
        if let positionDict = dictionary["position"] as? [AnyHashable: Any] {
            positionObj = Position(dictionary: positionDict)
        }else {
            positionObj = nil
        }
        self.position = positionObj
        
        let birthYearObj: BirthYear?
        if let birthYearDict = dictionary["birthYear"] as? [AnyHashable: Any] {
            birthYearObj = BirthYear(dictionary: birthYearDict)
        } else {
            birthYearObj = nil
        }
        self.birthYear = birthYearObj
        
        super.init(dictionary: dictionary)

    }
    
    public func toDictionary() -> NSDictionary {
        var dict = ["name": self.name,
                    "hiredMonth": self.hiredMonth
                    ] as [AnyHashable : Any]
        if let nonNullAge = self.age {
            dict["age"] = nonNullAge
        }
        
        if let nonNullPosition = self.position {
            dict["position"] = nonNullPosition.toDictionary()
        }
        
        if let nonNullStatus = self.status{
            dict["status"] = nonNullStatus.toDictionary()
        }
        
        if let nonnullBirthYear = self.birthYear {
            dict["birthYear"] = nonnullBirthYear.toDictionary()
        }   
        
        return dict as NSDictionary
    }
}
