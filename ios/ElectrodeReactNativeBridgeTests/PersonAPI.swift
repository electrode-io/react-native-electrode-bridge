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

import Foundation

public class PersonAPI {
    static let kEventPersonAdded = "kEventPersonAdded"
    static let kEventPersonNameUpdated = "kEventPersonNameUpdated"
    static let kRequestGetPerson = "kRequestGetPerson"
    static let kRequestGetStatus = "kRequestGetStatus"
    static let kRequestGetUserName = "kRequestGetUserName"
    static let kRequestGetAge = "kRequestGetAge"
    static let kRequestPostPersonUpdate = "kRequestPostPersonUpdate"
    static let kRequestFindPersonsByStatus = "kRequestFindPersonsByStatus"
    static let kRequestFindPersonsByName = "kRequestFindPersonsByName"
    lazy var event: Events = { PersonEvents() }()
    lazy var request: Requests = { PersonRequests() }()

    public class Events {
        func addPersonAddedEventListenr(eventListener _: @escaping ElectrodeBridgeEventListener) {
            assertionFailure("need override")
        }

        func addPersonNameUpdatedEventListener(eventListener _: @escaping ElectrodeBridgeEventListener) {
            assertionFailure("need override")
        }

        func emitEventPersonAdded(person _: Person) {
            assertionFailure("need override")
        }

        func emitEventPersonNameUpdated(updatedName _: String) {
        }
    }

    public class Requests {
        /***
         * Registers a handler that returns the current user when {@link #getPerson(ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        func registerGetPersonRequestHandler(handler _: @escaping ElectrodeBridgeRequestCompletionHandler) {
            assertionFailure("need override")
        }

        /**
         * Registers a handler  that returns the user status when {@link #getStatus(Person, ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        func registerGetStatusRequestHandler(handler _: @escaping ElectrodeBridgeRequestCompletionHandler) {
            assertionFailure("need override")
        }

        func registerGetAgeRequestHandler(handler _: @escaping ElectrodeBridgeRequestCompletionHandler) {
            assertionFailure("need override")
        }

        func registerUpdatePersonRequestHandler(handler _: @escaping ElectrodeBridgeRequestCompletionHandler) {
            assertionFailure("need override")
        }

        func registerFindPersonsByStatus(handler _: @escaping ElectrodeBridgeRequestCompletionHandler) {
            assertionFailure("need override")
        }

        func registerFindPersonsAgeByName(handler _: @escaping ElectrodeBridgeRequestCompletionHandler) {
            assertionFailure("need override")
        }

        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Person>#onSuccess({@link Person})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
         *
         * @param response {@link ElectrodeBridgeResponseListener<Person>} Request listener as a call back to be called once the operation is completed.
         */
        func getPerson(responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }

        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Status>#onSuccess({@link Status})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
         *
         * @param response {@link ElectrodeBridgeResponseListener<Status>} Request listener as a call back to be called once the operation is completed.
         */
        func getStatus(person _: Person, responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }

        func getUserName(responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }

        func getAge(name _: String, responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }

        func updatePersonPost(updatePersonRequestData _: UpdatePersonRequestData,
                              responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }

        func findPersonsByStatus(statusList _: [Status],
                                 responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }

        func findPersonByAge(names _: [String],
                             responseCompletionHandler _: @escaping ElectrodeBridgeResponseCompletionHandler) {
            assertionFailure("need override")
        }
    }
}
