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
    let personAPI = APersonAPI()

    
    func testSampleRequestNativeToNativeFailure() {
        let asyncExpectation = expectation(description: "testSampleRequestNativeToNativeFailure")

        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTFail()
        }, failureCallBack: { (failureMessage) in
            asyncExpectation.fulfill()
        })
        self.personAPI.request.getUserName(responseListner: responseListener)
        waitForExpectations(timeout: 10)
    }
    
    func testSampleRequestNativeToJSSuccess() {
        
        let asyncExpectation = expectation(description: "testSampleRequestNativeToJSSuccess")

        let expectedResults = "Your boss"
        let expectedResponseWithoutId = [kElectrodeBridgeMessageName : kRequestGetUserName,
                                kElectrodeBridgeMessageType:kElectrodeBridgeMessageResponse,
                                kElectrodeBridgeMessageData: expectedResults ]
            
        let mockJSListener = MockJSEeventListener(request: {(request) in
            XCTAssertNotNil(request)
         
        }, response: expectedResponseWithoutId)

        self.addMockEventListener(mockJSListener, forName: kRequestGetUserName)
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        self.personAPI.request.getUserName(responseListner: responseListener)
        waitForExpectations(timeout: 10)

    }
    
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
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        
        self.personAPI.request.registerGetStatusRequestHandler(handler: requestHandler)
        self.personAPI.request.getStatus(person: person, responseListener: responseListener)
        waitForExpectations(timeout: 10)

    }
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
