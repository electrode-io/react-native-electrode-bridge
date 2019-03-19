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

package com.walmartlabs.electrode.reactnative.sample.api;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEventListener;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerHandle;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.List;

public final class PersonApi {

    private static final Requests REQUESTS;
    private static final Events EVENTS;

    static {
        REQUESTS = new PersonRequests();
        EVENTS = new PersonEvents();
    }

    private PersonApi() {

    }

    @NonNull
    public static Requests requests() {
        return REQUESTS;
    }

    @NonNull
    public static Events events() {
        return EVENTS;
    }


    public interface Events {

        String EVENT_PERSON_ADDED = "com.apisample.ern.apisample.person.added";
        String EVENT_PERSON_NAME_UPDATED = "com.apisample.ern.apisample.person.name.updated";

        /**
         * Adds an event listener to the bridge for event{@link #EVENT_PERSON_ADDED}. Event listener's {@link ElectrodeBridgeEventListener#onEvent(Object)} will be invoked when the event happens.
         *
         * @param eventListener {@link ElectrodeBridgeEventListener}
         */
        void addPersonAddedEventListener(@NonNull final ElectrodeBridgeEventListener<Person> eventListener);

        /**
         * Adds an event listener to the bridge for event{@link #EVENT_PERSON_NAME_UPDATED}. Event listener's {@link ElectrodeBridgeEventListener#onEvent(Object)} will be invoked when the event happens.
         *
         * @param eventListener {@link ElectrodeBridgeEventListener}
         */
        void addPersonNameUpdatedEventListener(@NonNull final ElectrodeBridgeEventListener<String> eventListener);

        /**
         * Calling this method will emit an event {@link #EVENT_PERSON_ADDED} to all the registered {@link ElectrodeBridgeEventListener}(s).
         *
         * @param person {@link Person}
         */
        void emitEventPersonAdded(@NonNull Person person);

        /**
         * Calling this method will emit an event {@link #EVENT_PERSON_NAME_UPDATED} to all the registered {@link ElectrodeBridgeEventListener}(s).
         *
         * @param updatedName {@link String}
         */
        void emitEventPersonNameUpdated(@NonNull String updatedName);
    }

    public interface Requests {

        String REQUEST_GET_PERSON = "com.apisample.ern.apisample.get.person";
        String REQUEST_GET_STATUS = "com.apisample.ern.apisample.get.status";
        String REQUEST_GET_USER_NAME = "com.apisample.ern.apisample.get.user.name";
        String REQUEST_GET_AGE = "com.apisample.ern.apisample.get.age";
        String REQUEST_POST_PERSON_UPDATE = "com.apisample.ern.apisample.post.update.person";
        String REQUEST_FIND_PERSONS_BY_STATUS = "com.apisample.ern.apisample.find.persons.by.status";
        String REQUEST_FIND_PERSONS_AGE_BY_NAME = "com.apisample.ern.apisample.find.persons.age.by.name";

        /***
         * Registers a handler that returns the current user when {@link #getPerson(ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        RequestHandlerHandle registerGetPersonRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<None, Person> handler);

        /**
         * Registers a handler  that returns the user status when {@link #getStatus(Person, ElectrodeBridgeResponseListener)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        RequestHandlerHandle registerGetStatusRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<Person, Status> handler);

        RequestHandlerHandle registerGetAgeRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<String, Integer> handler);

        RequestHandlerHandle registerUpdatePersonRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<UpdatePersonRequestData, Person> handler);

        RequestHandlerHandle registerFindPersonsByStatus(@NonNull final ElectrodeBridgeRequestHandler<List<Status>, List<Person>> handler);

        RequestHandlerHandle registerFindPersonsAgeByName(@NonNull final ElectrodeBridgeRequestHandler<List<String>, List<Integer>> handler);


        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Person>#onSuccess({@link Person})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
         *
         * @param response {@link ElectrodeBridgeResponseListener<Person>} Request listener as a call back to be called once the operation is completed.
         */
        void getPerson(@NonNull final ElectrodeBridgeResponseListener<Person> response);

        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link ElectrodeBridgeResponseListener<Status>#onSuccess({@link Status})} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} call backs based on the result.
         *
         * @param response {@link ElectrodeBridgeResponseListener<Status>} Request listener as a call back to be called once the operation is completed.
         */
        void getStatus(@NonNull Person person, @NonNull final ElectrodeBridgeResponseListener<Status> response);


        void getUserName(@NonNull final ElectrodeBridgeResponseListener<String> response);

        void getAge(@NonNull String name, @NonNull final ElectrodeBridgeResponseListener<Integer> responseListener);

        void updatePersonPost(@NonNull UpdatePersonRequestData updatePersonRequestData, @NonNull final ElectrodeBridgeResponseListener<Person> responseListener);

        void findPersonsByStatus(@NonNull List<Status> statusList, @NonNull final ElectrodeBridgeResponseListener<List<Person>> responseListener);

        void findPersonsAgeByName(@NonNull List<String> names, @NonNull final ElectrodeBridgeResponseListener<List<Integer>> responseListener);
    }

}
