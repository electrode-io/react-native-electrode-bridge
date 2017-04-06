//
//  ElectrodeUtilities.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/9/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

let kElectrodeBridgeRequestTimeoutTime = 10;

let objectiveCPrimitives = [String.self,
                            Double.self,
                            Float.self,
                            Bool.self,
                            Int.self,
                            Int8.self,
                            Int16.self,
                            Int32.self,
                            Int64.self] as [Any.Type]

enum Property<AnyClass> {
    case Class(AnyClass)
    case Struct
}


public enum GenerateObjectError: Error {
    case arrayTypeMissmatch
    case emptyArrayItemType
    case unsupportedType
    case unBridgeable
    case deserializationError
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
    
    //TODO: add throws for exception handling
    static func generateObjectFromDict(data: [AnyHashable: Any], passedClass: AnyClass) throws -> AnyObject {
        let obj = (passedClass as? NSObject.Type)!
        let res = obj.init()
        let aMirrorChildren = Mirror(reflecting: res).children
        for case let(label, _) in aMirrorChildren {
            let tmpType = res.getTypeOfProperty(label!)! //Claire Todo: FIX force unwrapp
            switch(tmpType) {
            case .Class(let classType):
                let nextVal = data[label!]
                let obj = try NSObject.generateObject(data: nextVal as AnyObject , classType: classType as! AnyClass)
                
                res.setValue(obj, forKey: label!)
                print(classType)
            case .Struct:
                print(tmpType)
                let actualValue = data[label!]
                res.setValue(actualValue, forKey: label!)
            }
        }
        return res
    }
    
    // assume data has to be NSObject and return type has to be NSObject too
    // TODO: check with Deepu. What is it's a List of [AddressObject, primitives]
    // how to handle BOOL ?
    public static func generateObject(data: Any, classType: Any.Type, itemType: Any.Type? = nil) throws -> Any {
        var res: Any
        print(type(of:data))
        
        // check to see if the type already matches. so no need to serialize or deserialize 
        if (type(of:data) == classType && !(data is Array<Any>)) {
            return data
        }
        
        if(ElectrodeUtilities.isObjectiveCPrimitives(type: classType)) {
            res = data
        } else if (data is NSDictionary) {
            if let convertableData = data as? [AnyHashable: AnyObject] {
                print(classType)
                let obj =  try NSObject.generateObjectFromDict(data: convertableData, passedClass: classType as! AnyClass)
                res = obj
            } else {
                assertionFailure("failed here")
                return NSString()
            }
        } else if (data is Array<Any> ){
            if let arrayData = data as? Array<Any> {
                var tmpRes = Array<AnyObject>()
                guard let validItemType = itemType else { throw GenerateObjectError.emptyArrayItemType}
                print("valid item type is \(validItemType)")
                for item in arrayData {
                    var obj: AnyObject
                    if (ElectrodeUtilities.isObjectiveCPrimitives(type: validItemType)) {
                        obj = item as AnyObject
                    } else {
                        obj = try NSObject.generateObject(data: item as AnyObject, classType: validItemType) as AnyObject
                    }
                    tmpRes.append(obj)
                }
                res = tmpRes as AnyObject
            } else {
                throw GenerateObjectError.unsupportedType
            }
        } else {
            throw GenerateObjectError.unsupportedType
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
    
    static func isObjectiveCPrimitives(type: Any.Type) -> Bool {
        return (objectiveCPrimitives.contains(where: { (aClass) -> Bool in
            return aClass == type
        }))
    }
    
}
