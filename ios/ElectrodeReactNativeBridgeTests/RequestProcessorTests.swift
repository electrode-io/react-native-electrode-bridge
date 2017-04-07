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
    
    func testSampleRequestNativeToNativeFailure() {
        let asyncExpectation = expectation(description: "testSampleRequestNativeToNativeFailure")

        let personAPI = APersonAPI()
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTFail()
        }, failureCallBack: { (failureMessage) in
            asyncExpectation.fulfill()
        })
        personAPI.request.getUserName(responseListner: responseListener)
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
