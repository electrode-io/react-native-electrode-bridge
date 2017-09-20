//
//  None.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/6/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

public class None: NSObject, Bridgeable {
    public func toDictionary() -> NSDictionary {
        return NSDictionary()
    }

    public init(emptyData _: Data?) {
    }
}
