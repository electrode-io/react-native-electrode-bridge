//
//  Person.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//


public class Person: NSObject, Bridgeable {
    private static let tag = String(describing: type(of: self))
    
    var name: String
    var age: Int?
    var hiredMonth: Int
    var status: Status?
    var position: Position?
    var birthYear: BirthYear?
    
    init(name: String, age: Int?, hiredMonth: Int, status: Status?, position: Position?, birthYear: BirthYear?) {
        self.name = name
        self.age = age
        self.hiredMonth = hiredMonth
        self.status = status
        self.position = position
        self.birthYear = birthYear
        super.init()
    }
    
    override init() {
        self.name = String()
        self.age = nil
        self.hiredMonth = Int()
        self.status = nil
        self.position = nil
        self.birthYear = nil
        super.init()
    }
    
    
    convenience init?(dictionary: [String: Any]?) {
        guard let nonNullDictionary = dictionary else {
            return nil
        }
        
        guard let name = nonNullDictionary["name"] as? String else {
            assertionFailure("\(Person.tag) need name property")
            return nil
        }
        
        guard let hiredMonth = nonNullDictionary["month"] as? Int else {
            assertionFailure("\(Person.tag) need month property")
            return nil
        }
        
        //optional params
        let age = nonNullDictionary["age"] as? Int?
        
        let statusObj: Status?
        guard let statusDict = nonNullDictionary["month"] as? [String: Any] else {
            statusObj = nil
            return
        }
        statusObj = Status(dictionary: statusDict)
        
        let positionObj: Position?
        guard let positionDict = nonNullDictionary["position"] as? [String: Any] else {
            positionObj = nil
            return
        }
         positionObj = Position(dictionary: positionDict)
        
        let birthYearObj: BirthYear?
        guard let birthYearDict = nonNullDictionary["birthYear"] as? [String: Any] else {
            birthYearObj = nil
            return
        }
        birthYearObj = BirthYear(dictionary: birthYearDict)
        
        self.init(name: name,
                  age: age,
                  hiredMonth: hiredMonth,
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
