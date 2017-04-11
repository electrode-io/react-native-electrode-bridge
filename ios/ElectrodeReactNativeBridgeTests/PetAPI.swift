//
//  PetAPI.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/11/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import UIKit

class PetAPI {
    var event: Events {
        get {
            return PetEvents()
        }
    }
    var request: Requests {
        get {
            return PetRequest()
        }
    }
    
    class Events {
        func doSomething() {
            assertionFailure("required override")
        }
    }
    
    class Requests {
        func sendMyRequest() {
            assertionFailure("required override")
        }
    }
}

class PetEvents: PetAPI.Events {
    override func doSomething() {
        print("pet is doing something in event")
    }
}

class PetRequest: PetAPI.Requests {
    override func sendMyRequest() {
        print("pet is sending request")
    }
}

class MyOwnPetEvents: PetAPI.Events {
    override func doSomething() {
        print("*&*&*&*&*&** own event pets")
    }
}

class MyOwnPetAPI: PetAPI {

}



