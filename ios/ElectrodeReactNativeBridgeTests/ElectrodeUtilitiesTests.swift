//
//  ElectrodeUtilitiesTests.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/2/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import XCTest

class ElectrodeUtilitiesTests: XCTestCase {
    
    ////primitive int //////////////////////////////////////////////////
    //let convertAnyObjectAsInput = primitiveData as AnyObject
    //let returnType = type(of:primitiveData)
    //let primitiveGen = try? NSObject.generateObject(data: convertAnyObjectAsInput, classType: returnType)
    //////////////////////////////////////////////////////
    ////primitive string //////////////////////////////////////////////////
    //let anyObjectOfprimitiveString = primitiveString as AnyObject
    //let strType = type(of:primitiveData)
    //let primitiveStringGen = try? NSObject.generateObject(data: anyObjectOfprimitiveString, classType: strType)
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
