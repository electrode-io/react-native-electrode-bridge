//
//  ElectrodeUtilitiesTests.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/2/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import XCTest

@testable import ElectrodeReactNativeBridge

class ElectrodeUtilitiesTests: XCTestCase {
    

    func testGenerateObjectWithIntInput() {
        let primitiveData = 2
        let returnType = type(of:primitiveData)
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
        let addressDict = ["street": "860", "zipcode":"94086"]
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
        let addressWrapperDict = ["address": ["street": "860", "zipcode":"94086"]]
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
        let addressArray = [["street": "a", "zipcode":"94086"], ["street": "b", "zipcode":"94087"]]
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
}

@objc class AddressWrapper:  NSObject, Bridgeable {
    var address: Address = Address()
    
    func toDictionary() -> NSDictionary {
        var dict = [AnyHashable: Any]()
        dict["address"] = address.toDictionary()
        return dict as NSDictionary
    }
}

@objc class AddressArrayWrapper:NSObject {
    var addresses: [Address]  = [Address(), Address()]
}

@objc class Address: NSObject, Bridgeable {
    var street: String
    var zipcode: String
    
    init(street: String, zipcode: String) {
        self.street = street
        self.zipcode = zipcode
    }
    
    
    override init() {
        street = ""
        zipcode = ""
    }
    
    func toDictionary() -> NSDictionary {
        var dict = [AnyHashable: Any]()
        dict["street"] = street
        dict["zipcode"] = zipcode
        return dict as NSDictionary
    }
    
}
