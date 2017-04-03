//
//  ElectrodeUtilities.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/9/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

let kElectrodeBridgeRequestTimeoutTime = 10;


enum Property<AnyClass> {
    case Class(AnyClass)
    case Struct
}

extension NSObject {
    
    // Returns the property type
    func getTypeOfProperty (_ name: String) -> Property<Any>? {
        
        var type: Mirror = Mirror(reflecting: self)
        
        for child in type.children {
            if child.label! == name {
                let res = type(of: child.value)
                let tmp = ElectrodeUtilities.isSupportedPrimitive(type: res)
                return (!tmp) ? .Class(res) : .Struct
                
            }
        }
        while let parent = type.superclassMirror {
            for child in parent.children {
                if child.label! == name {
                    let res = type(of: child.value)
                    let tmp = ElectrodeUtilities.isSupportedPrimitive(type: res)
                    return (tmp) ? .Class(res) : .Struct
                }
            }
            type = parent
        }
        return nil
    }
    
    func toNSDictionary() -> NSDictionary {
        let type: Mirror = Mirror(reflecting: self)
        var res = [AnyHashable: Any]()
        for case let(label, value) in type.children {
            res[label!] = value
        }
        return res as NSDictionary
    }
    
    static func generateObject(data: [AnyHashable: Any], passedClass: AnyClass) -> AnyObject? {
        let obj = (passedClass as? NSObject.Type)!
        let res = obj.init()
        let aMirrorChildren = Mirror(reflecting: res).children
        for case let(label, value) in aMirrorChildren {
            let tmpType = res.getTypeOfProperty(label!)!
            switch(tmpType) {
            case .Class(let classType):
                guard let tmpValue = value as? Bridgeable else {
                    assertionFailure("is not bridgeable")
                    return nil
                }
                
                let dictValue = tmpValue.toDictionary() as! [AnyHashable : Any]
                let obj = NSObject.generateObject(data: dictValue , passedClass: classType as! AnyClass)
                
                res.setValue(obj, forKey: label!)
                print(classType)
            case .Struct:
                print(tmpType)
                res.setValue(value, forKey: label!)
            }
        }
        return res
    }
}


@objc class ElectrodeUtilities: NSObject {

    
    static func primitiveSet() -> Set<String> {
        let des = String(describing: type(of: String.self))
        let b = String(describing: type(of: Int.self))
        let c = String(describing: type(of: [String]?.self))
        return Set([des, b, c])
    }
    
    static func isSupportedPrimitive(type: Any.Type) -> Bool {
        let str = String(describing: type(of: type))
        return ElectrodeUtilities.primitiveSet().contains(str)
    }
    
    
}
