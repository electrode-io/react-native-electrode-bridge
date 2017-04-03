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
    
    
    
    
    //dictionary object /////////////////////////////////////////////////////////////////
    //let anyAddress = addressData as AnyObject
    //let addressType = Address.self
    //let addressGen = try? NSObject.generateObject(data: anyAddress, classType: addressType)
    //guard let address = addressGen as? Address else {
    //    assertionFailure("failed")
    //    return
    //}
    //print(address.street)
    //print(address.zipcode)
    //dictionary wrapper object////////////////////////////////////////////////////
    /*
     let anyAddressWrapper = addressWrapperData as AnyObject
     let addressWrapperType = AddressWrapper.self
     let addressWrapperGen = try? NSObject.generateObject(data: anyAddressWrapper, classType: addressWrapperType)
     guard let wrapper = addressWrapperGen as? AddressWrapper else {
     assertionFailure("failed")
     return
     }
     print(wrapper)
     */
    /////////////////array primitives////
    /*
     let anystringArrayData = stringArrayData as AnyObject
     let anystringArrayDataType = Array<Any>.self
     let itemType = String.self
     let anystringArrayDataGen = try? NSObject.generateObject(data: anystringArrayData, classType: anystringArrayDataType, itemType: itemType)
     print(anystringArrayDataGen)
     */
    
    /*
    let anyaddressArrayData = addressArrayData as AnyObject
    let anyaddressArrayDataType = Array<Any>.self
    let itemType = Address.self
    let anyaddressArrayDataGen = try? NSObject.generateObject(data: anyaddressArrayData, classType: anyaddressArrayDataType, itemType: itemType)
    guard let addressArray = anyaddressArrayDataGen as? Array<Address> else {return}
    print(addressArray[0].street)
    print(addressArray[0].zipcode)
    print(addressArray[1].street)
    print(addressArray[1].zipcode)
   */
    
}


class AddressWrapper: NSObject, Bridgeable {
    var address: Address = Address()
}

class AddressArrayWrapper: NSObject, Bridgeable {
    var addresses: [Address]  = [Address(), Address()]
}

class Address: NSObject, Bridgeable {
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
    
}
