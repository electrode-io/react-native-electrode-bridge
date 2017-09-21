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

import UIKit

class PetAPI {
    var event: Events {
        return PetEvents()
    }

    var request: Requests {
        return PetRequest()
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
