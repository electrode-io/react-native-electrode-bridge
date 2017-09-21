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

class RequestProcessorTests: ElectrodeBridgeBaseTests {
    var personAPI: PersonAPI?

    override func setUp() {
        super.setUp()
        personAPI = PersonAPI()
    }

    func testSampleRequestNativeToNativeFailure() {
        let asyncExpectation = expectation(description: "testSampleRequestNativeToNativeFailure")

        personAPI?.request.getUserName(responseCompletionHandler: { data, failureMessage in
            if let data = data {
                XCTFail()
            } else {
                guard let failure = failureMessage else {
                    XCTFail()
                    return
                }
                asyncExpectation.fulfill()
            }
        })
        waitForExpectations(timeout: 10)
    }

    func testSampleRequestNativeToJSSuccess() {
        let asyncExpectation = expectation(description: "testSampleRequestNativeToJSSuccess")
        let expectedResults = "Your boss"
        let expectedResponseWithoutId = [
            kElectrodeBridgeMessageName: PersonAPI.kRequestGetUserName,
            kElectrodeBridgeMessageType: kElectrodeBridgeMessageResponse,
            kElectrodeBridgeMessageData: expectedResults,
        ]
        let mockJSListener = SwiftMockJSEeventListener(jSBlock: { result in
            XCTAssertNotNil(result)
            guard let requestName = result[kElectrodeBridgeMessageName] as? String else {
                XCTFail()
                return
            }
            XCTAssertEqual(requestName, PersonAPI.kRequestGetUserName)
        }, response: expectedResponseWithoutId)
        appendMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetUserName)
        personAPI?.request.getUserName(responseCompletionHandler: { any, failureMessage in
            XCTAssertNotNil(any)
            XCTAssertNil(failureMessage)
            guard let responseDict = any as? [AnyHashable: Any] else {
                XCTFail()
                return
            }

            XCTAssertEqual(expectedResults, responseDict[kElectrodeBridgeMessageData] as? String)
            asyncExpectation.fulfill()
        })

        waitForExpectations(timeout: 10)
        removeMockEventListener(withName: PersonAPI.kRequestGetUserName)
    }

    func testRegisterGetStatusRequestHandleNativeToNative() {
        let asyncExpectation = expectation(description: "testRegisterGetStatusRequestHandleNativeToNative")

        // let status = Status(log: true, member: true)
        let person = Person(name: "John", age: nil, hiredMonth: 5, status: nil, position: nil, birthYear: nil)
        let status = Status(log: true, member: true)
        personAPI?.request.registerGetStatusRequestHandler(handler: { data, responseCompletionHandler in
            XCTAssertNotNil(data)
            XCTAssertNotNil(responseCompletionHandler)
            guard let returnedPerson = data as? Person else {
                XCTFail()
                return
            }

            XCTAssertEqual(returnedPerson.name, person.name)
            XCTAssertEqual(returnedPerson.hiredMonth, person.hiredMonth)
            responseCompletionHandler(status, nil)
        })
        personAPI?.request.getStatus(person: person, responseCompletionHandler: { any, failureMessage in
            XCTAssertNotNil(any)
            XCTAssertNil(failureMessage)
            guard let returnedStatus = any as? Status else {
                XCTFail()
                return
            }
            XCTAssertEqual(returnedStatus.log, status.log)
            XCTAssertEqual(returnedStatus.member, status.member)
            asyncExpectation.fulfill()
        })
        waitForExpectations(timeout: 10)
    }

    func testGetStatusRequestHandlerNativeToJSSuccess() {
        let asyncExpectation = expectation(description: "testRegisterGetStatusRequestHandleNativeToNative")
        let actualPerson = Person(name: "John", age: 10, hiredMonth: 6, status: nil, position: nil, birthYear: nil)
        let expectedStatus = Status(log: false, member: true)
        let statusDict = expectedStatus.toDictionary()

        let expectedResponseWithoutId = [
            kElectrodeBridgeMessageName: PersonAPI.kRequestGetStatus,
            kElectrodeBridgeMessageType: kElectrodeBridgeMessageResponse,
            kElectrodeBridgeMessageData: statusDict,
        ] as [AnyHashable: Any]

        let mockJSListener = SwiftMockJSEeventListener(jSBlock: { result in
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

            guard let returnedPayload = returnedStatusDict[kElectrodeBridgeMessageData] as? [AnyHashable: Any] else {
                XCTFail()
                return
            }

            XCTAssertEqual(actualPerson.name, returnedPayload["name"] as? String)
            XCTAssertEqual(actualPerson.age, returnedPayload["age"] as? Int)
            XCTAssertEqual(actualPerson.hiredMonth, returnedPayload["hiredMonth"] as? Int)

        }, response: statusDict)

        appendMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetStatus)
        personAPI?.request.getStatus(person: actualPerson, responseCompletionHandler: { any, failureMessage in
            XCTAssertNotNil(any)
            XCTAssertNil(failureMessage)
            guard let returnedStatus = any as? Status else {
                XCTFail()
                return
            }

            XCTAssertEqual(returnedStatus.log, expectedStatus.log)
            XCTAssertEqual(returnedStatus.member, expectedStatus.member)
            asyncExpectation.fulfill()
        })

        waitForExpectations(timeout: 10)
        removeMockEventListener(withName: PersonAPI.kRequestGetStatus)
    }

    func testGetStatusRequestHandlerJSToNativeSuccess() {
        let asyncExpectation = expectation(description: "testGetStatusRequestHandlerJSToNativeSuccess")
        let person = Person(name: "Shiba", age: 3, hiredMonth: 11, status: nil, position: nil, birthYear: nil)
        let resultStatus = Status(log: true, member: false)
        let mockJSResponseListner = SwiftMockJSEeventListener(jSBlock: { result in
            XCTAssertNotNil(result)
            guard let returnedStatusDict = result as? [String: Any] else {
                XCTFail()
                return
            }
            guard let returnedPayload = returnedStatusDict[kElectrodeBridgeMessageData] as? [AnyHashable: Any] else {
                XCTFail()
                return
            }
            XCTAssertEqual(resultStatus.log, returnedPayload["log"] as! Bool)
            XCTAssertEqual(resultStatus.member, returnedPayload["member"] as! Bool)
            asyncExpectation.fulfill()
        })

        appendMockEventListener(mockJSResponseListner, forName: PersonAPI.kRequestGetStatus)
        personAPI?.request.registerGetStatusRequestHandler(handler: { data, responseCompletionHandler in
            XCTAssertNotNil(data)
            guard let returnedPerson = data as? Person else {
                XCTFail()
                return
            }

            XCTAssertEqual(returnedPerson.name, person.name)
            XCTAssertEqual(returnedPerson.hiredMonth, person.hiredMonth)
            XCTAssertEqual(returnedPerson.age, person.age)
            responseCompletionHandler(resultStatus, nil)
        }
        )
        let requestPayload = [
            "name": person.name,
            "hiredMonth": person.hiredMonth,
            "age": person.age,
        ] as [AnyHashable: Any]
        let requestDict = [
            kElectrodeBridgeMessageId: UUID().uuidString,
            kElectrodeBridgeMessageType: "req",
            kElectrodeBridgeMessageName: PersonAPI.kRequestGetStatus,
            kElectrodeBridgeMessageData: requestPayload,
        ] as [AnyHashable: Any]

        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)
        waitForExpectations(timeout: 10)
    }

    func testPrimitiveTypesForRequestAndResponseNativeToNative() {
        let asyncExpectation = expectation(description: "testPrimitiveTypesForRequestAndResponseNativeToNative")
        personAPI?.request.registerGetAgeRequestHandler(handler: { payload, responseCompletionHandler in
            XCTAssertNotNil(payload)
            XCTAssertNotNil(responseCompletionHandler)
            XCTAssertEqual(payload as! String, "San Francisco")
            responseCompletionHandler(100, nil)
        })

        personAPI?.request.getAge(name: "San Francisco", responseCompletionHandler: { any, failureMessage in
            XCTAssertNotNil(any)
            XCTAssertNil(failureMessage)
            XCTAssertEqual(any as! Int, 100)
            asyncExpectation.fulfill()
        })
        waitForExpectations(timeout: 10)
    }

    func testIntegerForResponseNativeToJS() {
        let expectedResponse = 302
        let asyncExpectation = expectation(description: "testIntegerForResponseNativeToJS")

        let mockJSListener = SwiftMockJSEeventListener(jSBlock: { result in
            XCTAssertNotNil(result)
            guard let requestName = result[kElectrodeBridgeMessageName] as? String else {
                XCTFail()
                return
            }
            XCTAssertEqual(requestName, PersonAPI.kRequestGetAge)
        }, response: expectedResponse)

        appendMockEventListener(mockJSListener, forName: PersonAPI.kRequestGetAge)
        personAPI?.request.getAge(name: "SF", responseCompletionHandler: { any, failureMessage in
            XCTAssertNotNil(any)
            XCTAssertNil(failureMessage)
            XCTAssertEqual(any as! Int, expectedResponse)
            asyncExpectation.fulfill()
        })
        waitForExpectations(timeout: 10)
    }

    func testIntegerForResponseJSToNative() {
        let asyncExpectation = expectation(description: "testIntegerForResponseNativeToJS")
        let requestPayload = "testName"
        let expectedAge = 3

        let requestDict = [
            kElectrodeBridgeMessageId: UUID().uuidString,
            kElectrodeBridgeMessageType: "req",
            kElectrodeBridgeMessageName: PersonAPI.kRequestGetAge,
            kElectrodeBridgeMessageData: requestPayload,
        ] as [AnyHashable: Any]

        let mockJSResponseListner = SwiftMockJSEeventListener(jSBlock: { result in
            XCTAssertNotNil(result)
            guard let responseDict = result as? [AnyHashable: Any] else {
                XCTFail()
                return
            }
            XCTAssertEqual(responseDict[kElectrodeBridgeMessageType] as! String, kElectrodeBridgeMessageResponse)
            XCTAssertEqual(responseDict[kElectrodeBridgeMessageData] as! Int, expectedAge)

            asyncExpectation.fulfill()
        })

        appendMockEventListener(mockJSResponseListner, forName: PersonAPI.kRequestGetAge)
        personAPI?.request.registerGetAgeRequestHandler(handler: { any, responseCompletionHandler in
            XCTAssertNotNil(any)
            XCTAssertNotNil(responseCompletionHandler)
            XCTAssertEqual(any as! String, "testName")
            responseCompletionHandler(expectedAge, nil)
        })
        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)
        waitForExpectations(timeout: 10)
    }

    func testGetPersonRequestSentFromJsWithEmptyDataInRequest() {
        let asyncExpectation = expectation(description: "testGetPersonRequestSentFromJsWithEmptyDataInRequest")

        let requestDict = [
            kElectrodeBridgeMessageId: UUID().uuidString,
            kElectrodeBridgeMessageType: kElectrodeBridgeMessageRequest,
            kElectrodeBridgeMessageName: PersonAPI.kRequestGetPerson,
        ] as [AnyHashable: Any]

        personAPI?.request.registerGetPersonRequestHandler(handler: { any, _ in
            XCTAssertNil(any)
            asyncExpectation.fulfill()
        })
        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)
        waitForExpectations(timeout: 10)
    }

    func testRequestsWithMultipleParamsNativeToNative() {
        let asyncExpectation = expectation(description: "testRequestsWithMultipleParamsNativeToNative")

        let status = Status(log: false, member: true)
        let firstName = "Apple"
        let lastName = "Pear"
        let updatePersonRequestData = UpdatePersonRequestData(firstName: firstName, lastName: lastName, status: status)
        personAPI?.request.registerUpdatePersonRequestHandler(handler: { any, responseCompletionHandler in
            XCTAssertNotNil(any)
            XCTAssertNotNil(responseCompletionHandler)
            guard let updateRequestData = any as? UpdatePersonRequestData else {
                XCTFail()
                return
            }

            let person = Person(name: "\(firstName)\(lastName)", age: nil, hiredMonth: 2, status: status, position: nil, birthYear: nil)
            responseCompletionHandler(person, nil)
        })

        personAPI?.request.updatePersonPost(updatePersonRequestData: updatePersonRequestData, responseCompletionHandler: { any, failureMessage in
            XCTAssertNotNil(any)
            XCTAssertNil(failureMessage)
            guard let person = any as? Person else {
                XCTFail()
                return
            }

            XCTAssertEqual(person.name, "\(firstName)\(lastName)")
            XCTAssertNotNil(person.status)
            XCTAssertEqual(status.log, person.status?.log)
            XCTAssertEqual(status.member, person.status?.member)
            asyncExpectation.fulfill()
        })
        waitForExpectations(timeout: 10)
    }

    func testRequestWithParamsAsListOfObjJStoNative() {
        let asyncExpectation = expectation(description: "testRequestWithParamsAsListOfObjJStoNative")
        personAPI?.request.registerFindPersonsByStatus(handler: { any, responseCompletionHandler in
            XCTAssertNotNil(any)
            XCTAssertNotNil(responseCompletionHandler)

            guard let statusList = any as? [Status] else {
                XCTFail()
                return
            }

            responseCompletionHandler(nil, nil)
            asyncExpectation.fulfill()
        })

        let requestStatusArray = [["log": true, "member": true], ["log": false, "member": false], ["log": false, "member": true]]

        let requestDict = [
            kElectrodeBridgeMessageId: UUID().uuidString,
            kElectrodeBridgeMessageType: "req",
            kElectrodeBridgeMessageName: PersonAPI.kRequestFindPersonsByStatus,
            kElectrodeBridgeMessageData: requestStatusArray,
        ] as [AnyHashable: Any]
        MockBridgeTransceiver.sharedInstance().sendMessage(requestDict)
        waitForExpectations(timeout: 10)
    }
}

// MARK: Helper
class SwiftMockJSEeventListener: MockJSEeventListener {
    override init(responseBlock: @escaping responseBlock) {
        super.init(responseBlock: responseBlock)
    }

    override init(eventBlock: @escaping evetBlock) {
        super.init(eventBlock: eventBlock)
    }

    override init(jSBlock: @escaping ElectrodeBaseJSBlock) {
        super.init(jSBlock: jSBlock)
    }

    override init(jSBlock: @escaping ElectrodeBaseJSBlock, response: Any?) {
        super.init(jSBlock: jSBlock, response: response)
    }
}
