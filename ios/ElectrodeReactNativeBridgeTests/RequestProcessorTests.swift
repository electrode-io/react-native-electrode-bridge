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
    var personAPI: PersonAPI? = nil

    override func setUp() {
        super.setUp()
        self.personAPI = PersonAPI()
    }
    func testSampleRequestNativeToNativeFailure() {
        let asyncExpectation = expectation(description: "testSampleRequestNativeToNativeFailure")

        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTFail()
        }, failureCallBack: { (failureMessage) in
            asyncExpectation.fulfill()
        })
        self.personAPI?.request.getUserName(responseListner: responseListener)
        waitForExpectations(timeout: 10)
    }
    
    func testSampleRequestNativeToJSSuccess() {
        
        let asyncExpectation = expectation(description: "testSampleRequestNativeToJSSuccess")

        let expectedResults = "Your boss"
        let expectedResponseWithoutId = [kElectrodeBridgeMessageName : PersonAPI.kRequestGetUserName,
                                kElectrodeBridgeMessageType:kElectrodeBridgeMessageResponse,
                                kElectrodeBridgeMessageData: expectedResults ]
            
        
        let mockJSListener = SwiftMockJSEeventListener(jSBlock: {(result) in
            XCTAssertNotNil(result)
            guard let requestName = result[kElectrodeBridgeMessageName] as? String else{
                XCTFail()
                return
            }
            XCTAssertEqual(requestName, PersonAPI.kRequestGetUserName)
        }, response: expectedResponseWithoutId)

        self.appendMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetUserName)
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTAssertNotNil(any)
            XCTAssertEqual(expectedResults, any as? String)
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        self.personAPI?.request.getUserName(responseListner: responseListener)
        waitForExpectations(timeout: 10)
        self.removeMockEventListener(withName: PersonAPI.kRequestGetUserName)

    }
    
    func testRegisterGetStatusRequestHandleNativeToNative() {
        let asyncExpectation = expectation(description: "testRegisterGetStatusRequestHandleNativeToNative")

        //let status = Status(log: true, member: true)
        let person = Person(name: "John", age: nil, hiredMonth: 5, status: nil, position: nil, birthYear: nil)
        let status = Status(log: true, member: true)
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
        
        self.personAPI?.request.registerGetStatusRequestHandler(handler: requestHandler)
        self.personAPI?.request.getStatus(person: person, responseListener: responseListener)
        waitForExpectations(timeout: 10)

    }
    
    func testGetStatusRequestHandlerNativeToJSSuccess() {
        let asyncExpectation = expectation(description: "testRegisterGetStatusRequestHandleNativeToNative")
        let actualPerson = Person(name: "John", age: 10, hiredMonth: 6, status: nil, position: nil, birthYear: nil)
        let expectedStatus = Status(log: false, member: true)
        let statusDict = expectedStatus.toDictionary()
        
        let expectedResponseWithoutId = [kElectrodeBridgeMessageName : PersonAPI.kRequestGetStatus,
                                         kElectrodeBridgeMessageType:kElectrodeBridgeMessageResponse,
        kElectrodeBridgeMessageData: statusDict ] as [AnyHashable: Any]
        
                
        let mockJSListener = SwiftMockJSEeventListener(jSBlock: {(result) in
            XCTAssertNotNil(result)
            
            guard let returnedStatusDict = result as? [String: Any] else {
                XCTFail()
                return
            }
            
            guard let returnedRequestName = returnedStatusDict[kElectrodeBridgeMessageName] as? String else {
                XCTFail()
                return
            }
            XCTAssertEqual(returnedRequestName, PersonAPI.kRequestGetStatus)
            
            guard let returnedPayload = returnedStatusDict[kElectrodeBridgeMessageData] as? [AnyHashable: Any]else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(actualPerson.name, returnedPayload["name"] as? String)
            XCTAssertEqual(actualPerson.age, returnedPayload["age"] as? Int)
            XCTAssertEqual(actualPerson.hiredMonth, returnedPayload["hiredMonth"] as? Int)
            
        }, response: expectedResponseWithoutId)
        
        self.appendMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetStatus)
        
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTAssertNotNil(any)
            guard let returnedStatus = any as? Status else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(returnedStatus.log, expectedStatus.log)
            XCTAssertEqual(returnedStatus.member, expectedStatus.member)
            asyncExpectation.fulfill()
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        self.personAPI?.request.getStatus(person: actualPerson, responseListener: responseListener)
        
        waitForExpectations(timeout: 10)
        self.removeMockEventListener(withName: PersonAPI.kRequestGetStatus)
    }
    
    func testGetStatusRequestHandlerJSToNativeSuccess() {
        let asyncExpectation = expectation(description: "testGetStatusRequestHandlerJSToNativeSuccess")
        let person = Person(name: "Shiba", age: 3, hiredMonth: 11, status: nil, position: nil, birthYear: nil)
        let resultStatus = Status(log: true, member: false)
        
        let requestHandler = PersonResponseRequestHandler(completionClosure: { (data, responseListener) in
            XCTAssertNotNil(data)
            guard let returnedPerson = data as? Person else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(returnedPerson.name, person.name)
            XCTAssertEqual(returnedPerson.hiredMonth, person.hiredMonth)
            XCTAssertEqual(returnedPerson.age, person.age)
            responseListener.onSuccess(resultStatus)
        })
        
        
        let mockJSResponseListner = SwiftMockJSEeventListener(jSBlock: {(result) in
            XCTAssertNotNil(result)
            guard let returnedStatusDict = result as? [String: Any] else {
                XCTFail()
                return
            }
            guard let returnedPayload = returnedStatusDict[kElectrodeBridgeMessageData] as? [AnyHashable: Any]else {
                XCTFail()
                return
            }
            XCTAssertEqual(resultStatus.log, returnedPayload["log"] as! Bool)
            XCTAssertEqual(resultStatus.member, returnedPayload["member"] as! Bool)
            asyncExpectation.fulfill()
        })

        
        self.appendMockEventListener(mockJSResponseListner, forName: PersonAPI.kRequestGetStatus)

        self.personAPI?.request.registerGetStatusRequestHandler(handler: requestHandler)
        let requestPayload = ["name": person.name,
                       "hiredMonth": person.hiredMonth,
                       "age":person.age] as [AnyHashable: Any]
        let requestDict = [kElectrodeBridgeMessageId: UUID().uuidString,
                       kElectrodeBridgeMessageType: "req",
                       kElectrodeBridgeMessageName: PersonAPI.kRequestGetStatus,
                       kElectrodeBridgeMessageData: requestPayload
        ] as [AnyHashable: Any]
        
        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)
        waitForExpectations(timeout: 10)

    }
    
    func testPrimitiveTypesForRequestAndResponseNativeToNative() {
        let asyncExpectation = expectation(description: "testPrimitiveTypesForRequestAndResponseNativeToNative")

        let requestHandler = PersonResponseRequestHandler { (payload, responseListener: ElectrodeBridgeResponseListener) in
            XCTAssertNotNil(payload)
            XCTAssertNotNil(responseListener)
            XCTAssertEqual(payload as! String, "San Francisco")
            responseListener.onSuccess(100)
        }
        
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTAssertNotNil(any)
            XCTAssertEqual(any as! Int, 100)
            asyncExpectation.fulfill()
            
        }, failureCallBack: { (failureMessage) in
            XCTFail()
        })
        
        self.personAPI?.request.registerGetAgeRequestHandler(handler: requestHandler)
        self.personAPI?.request.getAge(name: "San Francisco", responseListener: responseListener)
        waitForExpectations(timeout: 10)

    }
    
    func testIntegerForResponseNativeToJS() {
        let expectedResponse = 302
        let asyncExpectation = expectation(description: "testIntegerForResponseNativeToJS")

        let mockJSListener = SwiftMockJSEeventListener(jSBlock: {(result) in
            XCTAssertNotNil(result)
            guard let requestName = result[kElectrodeBridgeMessageName] as? String else{
                XCTFail()
                return
            }
            XCTAssertEqual(requestName, PersonAPI.kRequestGetAge)
        }, response: expectedResponse)
        
        self.appendMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetAge)
        
        let responseListener = PersonResponseResponseListener(successCallBack: { (res) in
            XCTAssertNotNil(res)
            XCTAssertEqual(res as! Int, expectedResponse)
            asyncExpectation.fulfill()
        }, failureCallBack: { (fail) in
            XCTFail()
        })
        
        self.personAPI?.request.getAge(name: "SF", responseListener: responseListener)
        waitForExpectations(timeout: 10)
    }
    
    func testIntegerForResponseJSToNative() {
        let asyncExpectation = expectation(description: "testIntegerForResponseNativeToJS")
        let requestPayload = "testName"
        let expectedAge = 3
        
        let requestDict = [kElectrodeBridgeMessageId: UUID().uuidString,
                           kElectrodeBridgeMessageType: "req",
                           kElectrodeBridgeMessageName: PersonAPI.kRequestGetAge,
                           kElectrodeBridgeMessageData: requestPayload
            ] as [AnyHashable: Any]

        let requestHandler = PersonResponseRequestHandler { (any, listener: ElectrodeBridgeResponseListener) in
            XCTAssertNotNil(any)
            XCTAssertNotNil(listener)
            XCTAssertEqual(any as! String, "testName")
            listener.onSuccess(expectedAge)
        }
        
        let mockJSResponseListner = SwiftMockJSEeventListener(jSBlock: {(result) in
            XCTAssertNotNil(result)
            guard let responseDict = result as? [AnyHashable: Any] else {
                XCTFail()
                return
            }
            XCTAssertEqual(responseDict[kElectrodeBridgeMessageType] as! String, kElectrodeBridgeMessageResponse)
            XCTAssertEqual(responseDict[kElectrodeBridgeMessageData] as! Int, expectedAge)
            
            asyncExpectation.fulfill()
        })
        
        
        
        self.appendMockEventListener(mockJSResponseListner, forName: PersonAPI.kRequestGetAge)
        self.personAPI?.request.registerGetAgeRequestHandler(handler: requestHandler)
        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)

        waitForExpectations(timeout: 10)
    }
    
    func testGetPersonRequestSentFromJsWithEmptyDataInRequest() {
        let asyncExpectation = expectation(description: "testGetPersonRequestSentFromJsWithEmptyDataInRequest")

        let requestDict = [kElectrodeBridgeMessageId: UUID().uuidString,
                           kElectrodeBridgeMessageType: kElectrodeBridgeMessageRequest,
                           kElectrodeBridgeMessageName: PersonAPI.kRequestGetPerson,
            ] as [AnyHashable: Any]
        
        let requestHandler = PersonResponseRequestHandler { (any, responseListner: ElectrodeBridgeResponseListener) in
            XCTAssertNil(any)
            asyncExpectation.fulfill()
        }
        
        
        self.personAPI?.request.registerGetPersonRequestHandler(handler: requestHandler)
        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)
        waitForExpectations(timeout: 10)
    }
    
    func testRequestsWithMultipleParamsNativeToNative() {
        let asyncExpectation = expectation(description: "testRequestsWithMultipleParamsNativeToNative")
        
        let status = Status(log: false, member: true)
        let firstName = "Apple"
        let lastName = "Pear"
        let updatePersonRequestData = UpdatePersonRequestData(firstName: firstName, lastName: lastName, status: status)
        
        
        let requestHandler = PersonResponseRequestHandler { (any, responseListener: ElectrodeBridgeResponseListener) in
            XCTAssertNotNil(any)
            guard let updateRequestData = any as? UpdatePersonRequestData else {
                XCTFail()
                return
            }
            
            let person = Person(name: "\(firstName)\(lastName)", age: nil, hiredMonth: 2, status: status, position: nil, birthYear: nil)
            responseListener.onSuccess(person)
        }
        
        self.personAPI?.request.registerUpdatePersonRequestHandler(handler: requestHandler)
        
        
        let responseListener = PersonResponseResponseListener(successCallBack: { (any) in
            XCTAssertNotNil(any)
            guard let person = any as? Person else {
                XCTFail()
                return
            }
            
            XCTAssertEqual(person.name, "\(firstName)\(lastName)")
            XCTAssertNotNil(person.status)
            XCTAssertEqual(status.log, person.status?.log)
            XCTAssertEqual(status.member, person.status?.member)
            asyncExpectation.fulfill()
        }, failureCallBack: { (failure) in
            XCTFail()
        })
        
        self.personAPI?.request.updatePersonPost(updatePersonRequestData: updatePersonRequestData, responseListener: responseListener)
        waitForExpectations(timeout: 10)

    }
}



//MARK: Helper
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

class SwiftMockJSEeventListener: MockJSEeventListener {
    override init(responseBlock: @escaping responseBlock) {
        super.init(responseBlock: responseBlock)
    }
    
    override init(eventBlock: @escaping evetBlock) {
        super.init(eventBlock: eventBlock)
    }
    
    override init(jSBlock : @escaping ElectrodeBaseJSBlock) {
        super.init(jSBlock: jSBlock)
    }
    override init(jSBlock : @escaping ElectrodeBaseJSBlock, response: Any? ) {
        super.init(jSBlock: jSBlock, response: response)
    }
}
