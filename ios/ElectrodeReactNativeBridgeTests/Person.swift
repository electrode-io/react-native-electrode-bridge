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

public class Person: ElectrodeObject, Bridgeable {
    private static let tag = String(describing: type(of: self))

    var name: String
    var age: Int?
    var hiredMonth: Int
    var status: Status?
    var position: Position?
    var birthYear: BirthYear?
    var addresses: [CompleteAddress]?

    public init(name: String, age: Int?, hiredMonth: Int, status: Status?, position: Position?, birthYear: BirthYear?, addresses: [CompleteAddress]? = nil) {
        self.name = name
        self.age = age
        self.hiredMonth = hiredMonth
        self.status = status
        self.position = position
        self.birthYear = birthYear
        self.addresses = addresses
        super.init()
    }

    public required init(dictionary: [AnyHashable: Any]) {
        if let name = dictionary["name"] as? String,
            let hiredMonth = dictionary["hiredMonth"] as? Int {
            self.name = name
            self.hiredMonth = hiredMonth
        } else {
            assertionFailure("\(Person.tag) need hiredMonth property")
            hiredMonth = dictionary["month"] as! Int
            name = dictionary["name"] as! String
        }

        if let generatedCompleteAddress = try? NSObject.generateObject(data: dictionary["addresses"], classType: Array<Any>.self, itemType: CompleteAddress.self),
            let completeAddressList = generatedCompleteAddress as? [CompleteAddress] {
            addresses = completeAddressList
        }

        // optional params
        let age = dictionary["age"] as? Int
        self.age = age

        let statusObj: Status?
        if let statusDict = dictionary["month"] as? [AnyHashable: Any] {
            statusObj = Status(dictionary: statusDict)
        } else {
            statusObj = nil
        }
        status = statusObj

        let positionObj: Position?
        if let positionDict = dictionary["position"] as? [AnyHashable: Any] {
            positionObj = Position(dictionary: positionDict)
        } else {
            positionObj = nil
        }
        position = positionObj

        let birthYearObj: BirthYear?
        if let birthYearDict = dictionary["birthYear"] as? [AnyHashable: Any] {
            birthYearObj = BirthYear(dictionary: birthYearDict)
        } else {
            birthYearObj = nil
        }
        birthYear = birthYearObj
        super.init(dictionary: dictionary)
    }

    public func toDictionary() -> NSDictionary {
        var dict = [
            "name": self.name,
            "hiredMonth": self.hiredMonth,
        ] as [AnyHashable: Any]

        if let nonNullAge = self.age {
            dict["age"] = nonNullAge
        }

        if let nonNullPosition = self.position {
            dict["position"] = nonNullPosition.toDictionary()
        }

        if let nonNullStatus = self.status {
            dict["status"] = nonNullStatus.toDictionary()
        }

        if let nonnullBirthYear = self.birthYear {
            dict["birthYear"] = nonnullBirthYear.toDictionary()
        }

        if let nonNullAddresses = self.addresses {
            dict["addresses"] = nonNullAddresses.map { address in
                address.toDictionary() }
        }

        return dict as NSDictionary
    }
}
