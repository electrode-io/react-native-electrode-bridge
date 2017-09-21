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

import XCTest
@testable import ElectrodeReactNativeBridge

class EventProcessorTests: ElectrodeBridgeBaseTests {

    func testEventsForModelObjectNativeToNative() {
        let asyncExpectation = expectation(description: "testEventsForModelObjectNativeToNative")

        let position = Position(lat: 2.3, lng: 3.1)
        let status = Status(log: false, member: true)
        let birthYear = BirthYear(month: 12, year: 2000)
        let person = Person(name: "A", age: 3, hiredMonth: 6, status: status, position: position, birthYear: birthYear)

        let personAPI = PersonAPI()
        personAPI.event.addPersonAddedEventListenr(eventListener: { any in
            XCTAssertNotNil(any)
            guard let person = any as? Person else {
                XCTFail()
                return
            }

            XCTAssertEqual(person.name, "A")
            XCTAssertEqual(person.age, 3)
            XCTAssertEqual(person.hiredMonth, 6)
            XCTAssertEqual(person.status?.log, false)
            XCTAssertEqual(person.status?.member, true)
            XCTAssertEqual(person.position?.lat, 2.3)
            XCTAssertEqual(person.position?.lng, 3.1)
            XCTAssertEqual(person.birthYear?.month, 12)
            XCTAssertEqual(person.birthYear?.year, 2000)
            asyncExpectation.fulfill()
        })
        personAPI.event.emitEventPersonAdded(person: person)
        waitForExpectations(timeout: 5)
    }

    func testEventsForPrimitivesNativeToNative() {
        let asyncExpectation = expectation(description: "testEventsForPrimitivesNativeToNative")
        let personAPI = PersonAPI()
        let personName = "Claire"
        personAPI.event.addPersonNameUpdatedEventListener(eventListener: { any in
            XCTAssertNotNil(any)

            guard let name = any as? String else {
                XCTFail()
                return
            }

            XCTAssertEqual(name, "Claire")
            asyncExpectation.fulfill()
        })
        personAPI.event.emitEventPersonNameUpdated(updatedName: personName)
        waitForExpectations(timeout: 10)
    }
}
