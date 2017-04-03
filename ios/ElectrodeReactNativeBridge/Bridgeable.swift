//
//  Bridgeable.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/3/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import Foundation

//@objc requires this protocol to be a class protocol
// mark public because ElectrodeBridgeMessage is public
@objc public protocol Bridgeable {
    @objc func toDictionary() -> NSDictionary
}

extension Bridgeable {
    func toDictionary() -> NSDictionary {
        let aMirror = Mirror(reflecting: self)
        var dict = [AnyHashable: Any]()
        for case let(label, value) in aMirror.children {
            guard let validLabel = label else {
                assertionFailure("label for object is not valid")
                return NSDictionary()
            }
            
            guard let obj = self as? NSObject else {
                assertionFailure("cannot bridge object to toDictionary")
                return NSDictionary()
            }
            
            guard let propertyType = obj.getTypeOfProperty(validLabel) else {
                assertionFailure("object has property of empty label")
                return NSDictionary()
            }
            switch(propertyType) {
            case .Class(let classType):
                guard let validValue = value as? Bridgeable else {
                    assertionFailure("\(classType) is not bridgeable when it's required")
                    return NSDictionary()
                }
                dict[validLabel] = validValue.toDictionary()
                
            case .Struct:
                dict[validLabel] = value
            }
            
        }
        return dict as NSDictionary
    }
}
