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
        let person = Person(name: "A", age: 3, month: 11, status: status, position: position, birthYear: birthYear)
        let localListener = PersonEventListener { (any) in
            XCTAssertNotNil(any)
            guard let person = any as? Person else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(person.name, "A")
            XCTAssertEqual(person.age, 3)
            XCTAssertEqual(person.month, 11)
            XCTAssertEqual(person.status.log, false)
            XCTAssertEqual(person.status.member, true)
            XCTAssertEqual(person.position.lat, 2.3)
            XCTAssertEqual(person.position.lng, 3.1)
            XCTAssertEqual(person.birthYear.month, 12)
            XCTAssertEqual(person.birthYear.year, 2000)
            asyncExpectation.fulfill()
        }
        
        let personAPI = APersonAPI()
        personAPI.event.addPersonAddedEventListenr(eventListener: localListener)
        personAPI.event.emitEventPersonAdded(person: person)
        waitForExpectations(timeout: 10)
    }
    
    func testEventsForPrimitivesNativeToNative() {
        let asyncExpectation = expectation(description: "testEventsForPrimitivesNativeToNative")

        let localListener = PersonEventListener(validationBlock: { (any) in
            XCTAssertNotNil(any)
            
            guard let name = any as? String else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(name, "Claire")
            asyncExpectation.fulfill()
        })
        let personAPI = APersonAPI()
        let personName = "Claire"
        personAPI.event.addPersonNameUpdatedEventListener(eventListener: localListener)
        personAPI.event.emitEventPersonNameUpdated(updatedName: personName)
        waitForExpectations(timeout: 10)
    }
}

private class PersonEventListener: NSObject, ElectrodeBridgeEventListener {
    let validationBlock:(Any?) -> ()
    init(validationBlock: @escaping (Any?) -> ()) {
        self.validationBlock = validationBlock
    }
    func onEvent(_ eventPayload: Any?) {
        self.validationBlock(eventPayload)
    }
}
