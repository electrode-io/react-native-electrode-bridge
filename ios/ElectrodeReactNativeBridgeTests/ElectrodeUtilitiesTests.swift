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

import XCTest

@testable import ElectrodeReactNativeBridge

class ElectrodeUtilitiesTests: XCTestCase {

    func testGenerateObjectWithIntInput() {
        let primitiveData = 2
        let returnType = type(of: primitiveData)
        let primitiveGen = try? NSObject.generateObject(data: primitiveData as AnyObject, classType: returnType)
        guard let res = primitiveGen as? Int else {
            XCTAssert(false)
            return
        }

        XCTAssert(res == 2)
    }

    func testGenerateObjectWithStringAsInput() {
        let str = "MyString"
        let returnType = String.self
        let strGen = try? NSObject.generateObject(data: str, classType: returnType)
        guard let res = strGen as? String else {
            XCTAssert(false)
            return
        }
        XCTAssert(res == str)
    }

    func testGenerateObjectWithDicionaryAsInput() {
        let addressDict = ["street": "860", "zipcode": "94086"]
        let returnType = Address.self
        let addressGen = try? NSObject.generateObject(data: addressDict, classType: returnType)

        guard let res = addressGen as? Address else {
            XCTAssert(false)
            return
        }
        XCTAssert(res.street == "860")
        XCTAssert(res.zipcode == "94086")
    }

    func testGenerateObjectWithObjectInsideAnotherObjAsInput() {
        let addressWrapperDict = ["address": ["street": "860", "zipcode": "94086"]]
        let returnType = AddressWrapper.self
        let addressWrapperGen = try? NSObject.generateObject(data: addressWrapperDict, classType: returnType)

        guard let res = addressWrapperGen as? AddressWrapper else {
            XCTAssert(false)
            return
        }
        XCTAssert(res.address.isKind(of: Address.self))
        XCTAssert(res.address.zipcode == "94086")
        XCTAssert(res.address.street == "860")
    }

    func testGenerateObjectWithArrayOfPrimitivesAsInput() {
        let strArray = ["a", "b"]
        let returnType = Array<Any>.self
        let itemType = String.self
        let arrayPrimitivesGen = try? NSObject.generateObject(data: strArray, classType: returnType, itemType: itemType)
        guard let res = arrayPrimitivesGen as? [String] else {
            XCTAssert(false)
            return
        }
        XCTAssert(strArray.count == res.count)
        XCTAssert(res[0] == "a")
        XCTAssert(res[1] == "b")
    }

    func testGenerateObjectWithArrayOfComplexObjectAsInput() {
        let addressArray = [["street": "a", "zipcode": "94086"], ["street": "b", "zipcode": "94087"]]
        let returnType = Array<Any>.self
        let itemType = Address.self
        let arrayAddressGen = try? NSObject.generateObject(data: addressArray, classType: returnType, itemType: itemType)
        guard let res = arrayAddressGen as? [Address] else {
            XCTAssert(false)
            return
        }

        XCTAssert(res.count == addressArray.count)
        XCTAssert(res[0].street == "a")
        XCTAssert(res[0].zipcode == "94086")
        XCTAssert(res[1].street == "b")
        XCTAssert(res[1].zipcode == "94087")
    }

    func testGeneratePersonWithCompleteAddress() {
        guard let path = Bundle(for: type(of: self)).path(forResource: "Person", ofType: ".json") else {
            XCTFail("invalid path")
            return
        }
        let pathURL = URL(fileURLWithPath: path)
        let data = try! Data(contentsOf: pathURL)
        let json = try? JSONSerialization.jsonObject(with: data, options: [])

        guard let jsonDict = json as? NSDictionary else {
            XCTFail("not a valid json")
            return
        }

        let person = Person(dictionary: jsonDict as! [AnyHashable: Any])

        XCTAssert(person.addresses?.count == 2)
        XCTAssert(person.addresses?[0].city == "Sunnyvale")
        XCTAssert(person.addresses?[0].streetOne == "860 W California Ave")
        XCTAssert(person.addresses?[0].state == "California")
        XCTAssert(person.addresses?[1].city == "Mountain View")
    }
}

@objc class AddressWrapper: ElectrodeObject, Bridgeable {
    let address: Address

    required init(dictionary: [AnyHashable: Any]) {
        if let addressDict = dictionary["address"] as? [AnyHashable: Any] {
            address = Address(dictionary: addressDict)
        } else {
            assertionFailure("Failed")
            address = dictionary["address"] as! Address
        }
        super.init(dictionary: dictionary)
    }

    func toDictionary() -> NSDictionary {
        var dict = [AnyHashable: Any]()
        dict["address"] = address.toDictionary()
        return dict as NSDictionary
    }
}

@objc class Address: ElectrodeObject, Bridgeable {
    let street: String
    let zipcode: String

    init(street: String, zipcode: String) {
        self.street = street
        self.zipcode = zipcode
        super.init()
    }

    required init(dictionary: [AnyHashable: Any]) {
        if let street = dictionary["street"] as? String,
            let zipcode = dictionary["zipcode"] as? String {
            self.street = street
            self.zipcode = zipcode
        } else {
            assertionFailure("Missing required params")
            street = dictionary["street"] as! String
            zipcode = dictionary["zipcode"] as! String
        }
        super.init(dictionary: dictionary)
    }

    func toDictionary() -> NSDictionary {
        var dict = [AnyHashable: Any]()
        dict["street"] = street
        dict["zipcode"] = zipcode
        return dict as NSDictionary
    }
}
