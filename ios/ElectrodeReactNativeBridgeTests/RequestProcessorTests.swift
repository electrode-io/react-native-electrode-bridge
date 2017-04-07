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
