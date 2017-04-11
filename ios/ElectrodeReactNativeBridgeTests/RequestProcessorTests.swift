//
//  RequestProcessorTests.swift
//  ElectrodeReactNativeBridge
//
//  Created by Claire Weijie Li on 4/5/17.
//  Copyright © 2017 Walmart. All rights reserved.
//

import XCTest
@testable import ElectrodeReactNativeBridge

class RequestProcessorTests: ElectrodeBridgeBaseTests {
    let personAPI = PersonAPI()

    
    func testSampleRequestNativeToNativeFailure() {
        let asyncExpectation = expectation(description: "testSampleRequestNativeToNativeFailure")

        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTFail()
        }, failureCallBack: { (failureMessage) in
            asyncExpectation.fulfill()
        })
        self.personAPI.request.getUserName(responseListner: responseListener)
        waitForExpectations(timeout: 15)
    }
    
    func testSampleRequestNativeToJSSuccess() {
        
        let asyncExpectation = expectation(description: "testSampleRequestNativeToJSSuccess")

        let expectedResults = "Your boss"
        let expectedResponseWithoutId = [kElectrodeBridgeMessageName : PersonAPI.kRequestGetUserName,
                                kElectrodeBridgeMessageType:kElectrodeBridgeMessageResponse,
                                kElectrodeBridgeMessageData: expectedResults ]
            
        let mockJSListener = MockJSEeventListener(request: {(request) in
            XCTAssertNotNil(request)
         
        }, response: expectedResponseWithoutId)

        self.addMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetUserName)
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        self.personAPI.request.getUserName(responseListner: responseListener)
        waitForExpectations(timeout: 20)

    }
    /*
    func testRegisterGetStatusRequestHandleNativeToNative() {
        let asyncExpectation = expectation(description: "testRegisterGetStatusRequestHandleNativeToNative")

        //let status = Status(log: true, member: true)
        let person = Person(name: "John", age: nil, hiredMonth: 5, status: nil, position: nil, birthYear: nil)
        let status = Status(log: true, member: false)
        let requestHandler = PersonResponseRequestHandler(completionClosure: { (data, responseListener) in
            XCTAssertNotNil(data)
            guard let returnedPerson = data as? Person else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(returnedPerson.name, person.name)
            XCTAssertEqual(returnedPerson.hiredMonth, person.hiredMonth)
            responseListener.onSuccess(status)
        })
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTAssertNotNil(any)
            guard let returnedStatus = any as? Status else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(returnedStatus.log, status.log)
            XCTAssertEqual(returnedStatus.member, status.member)
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        
        self.personAPI.request.registerGetStatusRequestHandler(handler: requestHandler)
        self.personAPI.request.getStatus(person: person, responseListener: responseListener)
        waitForExpectations(timeout: 10)

    }
    
    func testGetStatusRequestHandlerNativeToJSSuccess() {
        let asyncExpectation = expectation(description: "testRegisterGetStatusRequestHandleNativeToNative")
        let actualPerson = Person(name: "John", age: 10, hiredMonth: 5, status: nil, position: nil, birthYear: nil)
        let expectedStatus = Status(log: true, member: true)
        let statusDict = expectedStatus.toDictionary()
        
        let expectedResponseWithoutId = [kElectrodeBridgeMessageName : PersonAPI.kRequestGetStatus,
                                         kElectrodeBridgeMessageType:kElectrodeBridgeMessageResponse,
        kElectrodeBridgeMessageData: statusDict ] as [AnyHashable: Any]
        
        let mockJSListener = MockJSEeventListener(request: {(request: ElectrodeBridgeRequestNew) in
            XCTAssertNotNil(request)
            XCTAssertEqual(PersonAPI.kRequestGetStatus, request.name)
            guard let requestPayload = request.data as? [AnyHashable: Any] else  {
                XCTFail()
                return
            }
            guard let person = try? NSObject.generateObject(data: requestPayload, classType: Person.self) as? Person else{
                XCTFail()
                return
            }
            XCTAssertNotNil(person)
            
            XCTAssertEqual(actualPerson.name, person?.name)
            XCTAssertEqual(actualPerson.age, person?.age)
            XCTAssertEqual(actualPerson.hiredMonth, person?.hiredMonth)
            
        }, response: expectedResponseWithoutId)
        
        self.addMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetStatus)
        
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTAssertNotNil(any)
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        self.personAPI.request.getStatus(person: actualPerson, responseListener: responseListener)
        waitForExpectations(timeout: 100)

    }
 */
}

private class PersonResponseResponseListener: NSObject, ElectrodeBridgeResponseListener {
    let failureBlock: (ElectrodeFailureMessage) -> ()
    let sucessBlock: (Any?) -> ()
    
    init(successCallBack: @escaping (Any?) -> (), failureCallBack: @escaping (ElectrodeFailureMessage) -> ()) {
        self.sucessBlock = successCallBack
        self.failureBlock = failureCallBack
    }
    
    func onSuccess(_ responseData: Any?) {
        sucessBlock(responseData)
    }
    
    func onFailure(_ failureMessage: ElectrodeFailureMessage) {
        failureBlock(failureMessage)
    }
}

private class PersonResponseRequestHandler: NSObject, ElectrodeBridgeRequestHandler  {
    let completionClosure: (_ data: Any?, _ responseListner: ElectrodeBridgeResponseListener) -> ()
    init(completionClosure: @escaping (_ data: Any?, _ responseListner: ElectrodeBridgeResponseListener) -> ()) {
        self.completionClosure = completionClosure
    }
    func onRequest(_ data: Any?, responseListener: ElectrodeBridgeResponseListener) {
        self.completionClosure(data, responseListener)
    }
}
