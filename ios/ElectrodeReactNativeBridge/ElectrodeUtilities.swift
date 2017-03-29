//
//  ElectrodeUtilities.swift
//  ElectrodeReactNativeBridge
//
//  Created by Cody Garvin on 3/9/17.
//  Copyright © 2017 Bharath Marulasiddappa. All rights reserved.
//

import UIKit

let kElectrodeBridgeRequestTimeoutTime = 10;

enum BridgeMessageType: String {
    case Data = "data"
    case Name = "name"
    case ID   = "id"
    case MessageType = "type"
}

class BridgeMessage: NSObject {
    
}

class ElectrodeUtilities: NSObject {
    static func generateObject<T>(payload: NSDictionary?, returnClass: T.Type) -> T? {
        var response: T? = nil
        
        guard let payload = payload else { return response }
        if !payload.allKeys.isEmpty {
            
            if payload.object(forKey: BridgeMessageType.Data.rawValue) != nil {
                
            }
        }
        
        return response
    }
}

protocol Bridgeable {
    func toDictionary() -> NSDictionary
    static func generateObject(_ data: Any?) -> AnyObject
}

extension Bridgeable {
    static func generateObject(_ data: Any?) -> AnyObject? {
        return nil
    }
}

/*
@Nullable
public static <T> T generateObject(@Nullable Bundle payload, @NonNull Class<T> returnClass) {
    T response = null;
    if (payload != null
        && !payload.isEmpty()) {
        String key = BridgeMessage.BRIDGE_MSG_DATA;
        
        if (payload.get(key) == null) {
            throw new IllegalArgumentException("Cannot find key(" + key + ") in given bundle:" + payload);
        }
        
        if (Bridgeable.class.isAssignableFrom(returnClass)) {
            
            if (payload.getBundle(key) == null) {
                throw new IllegalArgumentException("Value for key(" + key + ") should be a bundle, looks like it is not.");
            }
            
            response = BridgeArguments.bridgeableFromBundle(payload.getBundle(key), returnClass);
        } else {
            response = (T) BridgeArguments.getPrimitiveFromBundle(payload, returnClass);
        }
    }
    return response;
}*/
