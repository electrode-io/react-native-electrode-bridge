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

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerHandle;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerProcessor;
import com.walmartlabs.electrode.reactnative.bridge.RequestProcessor;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.List;

/***
 * This is a test API generated for testing the bridge. This is first generated using api gen commend and necessary changes are made.
 * <p>
 * This needs to be kept as a reference for generating the api code. This is how a generated API code looks like(format, order, naming conventions etc.)
 * <p>
 * This class provides all the request actions that can be performed on Person.
 */
final class PersonRequests implements PersonApi.Requests {

    PersonRequests() {

    }

    @Override
    public RequestHandlerHandle registerGetPersonRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<None, Person> handler) {
        return new RequestHandlerProcessor<>(REQUEST_GET_PERSON, None.class, Person.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerGetStatusRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<Person, Status> handler) {
        return new RequestHandlerProcessor<>(REQUEST_GET_STATUS, Person.class, Status.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerGetAgeRequestHandler(@NonNull ElectrodeBridgeRequestHandler<String, Integer> handler) {
        return new RequestHandlerProcessor<>(REQUEST_GET_AGE, String.class, Integer.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerUpdatePersonRequestHandler(@NonNull ElectrodeBridgeRequestHandler<UpdatePersonRequestData, Person> handler) {
        return new RequestHandlerProcessor<>(REQUEST_POST_PERSON_UPDATE, UpdatePersonRequestData.class, Person.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerFindPersonsByStatus(@NonNull ElectrodeBridgeRequestHandler<List<Status>, List<Person>> handler) {
        return new RequestHandlerProcessor<>(REQUEST_FIND_PERSONS_BY_STATUS, (Class) Status.class, (Class) Person.class, handler).execute();
    }

    @Override
    public RequestHandlerHandle registerFindPersonsAgeByName(@NonNull ElectrodeBridgeRequestHandler<List<String>, List<Integer>> handler) {
        return new RequestHandlerProcessor<>(REQUEST_FIND_PERSONS_AGE_BY_NAME, (Class) String.class, (Class) Integer.class, handler).execute();
    }

    @Override
    public void getPerson(@NonNull final ElectrodeBridgeResponseListener<Person> responseListener) {
        new RequestProcessor<>(REQUEST_GET_PERSON, null, Person.class, responseListener).execute();
    }


    @Override
    public void getStatus(@NonNull Person person, @NonNull final ElectrodeBridgeResponseListener<Status> responseListener) {
        new RequestProcessor<>(REQUEST_GET_STATUS, person, Status.class, responseListener).execute();
    }


    @Override
    public void getUserName(@NonNull final ElectrodeBridgeResponseListener<String> responseListener) {
        new RequestProcessor<None, String>(REQUEST_GET_USER_NAME, null, String.class, responseListener).execute();
    }

    @Override
    public void getAge(@NonNull String name, @NonNull final ElectrodeBridgeResponseListener<Integer> responseListener) {
        new RequestProcessor<>(REQUEST_GET_AGE, name, Integer.class, responseListener).execute();
    }

    @Override
    public void updatePersonPost(@NonNull UpdatePersonRequestData updatePersonRequestData, @NonNull final ElectrodeBridgeResponseListener<Person> responseListener) {
        new RequestProcessor<>(REQUEST_POST_PERSON_UPDATE, updatePersonRequestData, Person.class, responseListener).execute();
    }

    @Override
    public void findPersonsByStatus(@NonNull List<Status> statusList, @NonNull ElectrodeBridgeResponseListener<List<Person>> responseListener) {
        new RequestProcessor<>(REQUEST_FIND_PERSONS_BY_STATUS, statusList, (Class) List.class, Person.class, responseListener).execute();
    }

    @Override
    public void findPersonsAgeByName(@NonNull List<String> names, @NonNull ElectrodeBridgeResponseListener<List<Integer>> responseListener) {
        new RequestProcessor<>(REQUEST_FIND_PERSONS_AGE_BY_NAME, names, (Class) List.class, Integer.class, responseListener).execute();
    }
}
