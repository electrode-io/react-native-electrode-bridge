//
//  EventProcessorTests.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/4/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

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
