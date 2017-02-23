package com.walmartlabs.electrode.reactnative.sample.api;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeHolder;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.None;
import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerProcessor;
import com.walmartlabs.electrode.reactnative.bridge.RequestProcessor;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

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
    public void registerGetPersonRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<None, Person> handler) {
        ElectrodeBridgeHolder.registerRequestHandler(REQUEST_GET_PERSON, new RequestHandlerProcessor<>(None.class, Person.class, handler));
    }

    @Override
    public void registerGetStatusRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<Person, Status> handler) {
        ElectrodeBridgeHolder.registerRequestHandler(REQUEST_GET_STATUS, new RequestHandlerProcessor<>(Person.class, Status.class, handler));
    }

    @Override
    public void registerGetAgeRequestHandler(@NonNull ElectrodeBridgeRequestHandler<String, Integer> handler) {
        ElectrodeBridgeHolder.registerRequestHandler(REQUEST_GET_AGE, new RequestHandlerProcessor<>(String.class, Integer.class, handler));
    }

    @Override
    public void getPerson(@NonNull final ElectrodeBridgeResponseListener<Person> responseListener) {
        new RequestProcessor<>(REQUEST_GET_STATUS, null, Person.class, responseListener).execute();
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
}
