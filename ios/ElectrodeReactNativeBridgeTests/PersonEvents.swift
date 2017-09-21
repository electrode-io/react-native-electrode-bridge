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

public class PersonEvents: PersonAPI.Events {
    public override func addPersonAddedEventListenr(eventListener: @escaping ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: PersonAPI.kEventPersonAdded, eventPayloadClass: Person.self, eventListener: eventListener)
        listenerProcessor.execute()
    }

    public override func addPersonNameUpdatedEventListener(eventListener: @escaping ElectrodeBridgeEventListener) {
        let listenerProcessor = EventListenerProcessor(eventName: PersonAPI.kEventPersonNameUpdated, eventPayloadClass: String.self, eventListener: eventListener)
        listenerProcessor.execute()
    }

    public override func emitEventPersonAdded(person: Person) {
        let eventProcessor = EventProcessor(eventName: PersonAPI.kEventPersonAdded, eventPayload: person)
        eventProcessor.execute()
    }

    public override func emitEventPersonNameUpdated(updatedName: String) {
        let eventProcessor = EventProcessor(eventName: PersonAPI.kEventPersonNameUpdated, eventPayload: updatedName)
        eventProcessor.execute()
    }
}
