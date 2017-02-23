package com.walmartlabs.electrode.reactnative.sample.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.RequestHandlerConverter;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeHolder;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequest;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;
import com.walmartlabs.electrode.reactnative.bridge.FailureMessage;
import com.walmartlabs.electrode.reactnative.bridge.None;
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
        ElectrodeBridgeHolder.registerRequestHandler(REQUEST_GET_PERSON, new RequestHandlerConverter<>(None.class, Person.class, handler));
    }

    @Override
    public void registerGetStatusRequestHandler(@NonNull final ElectrodeBridgeRequestHandler<Person, Status> handler) {
        ElectrodeBridgeHolder.registerRequestHandler(REQUEST_GET_STATUS, new RequestHandlerConverter<>(Person.class, Status.class, handler));
    }

    @Override
    public void registerGetAgeRequestHandler(@NonNull ElectrodeBridgeRequestHandler<String, Integer> handler) {
        ElectrodeBridgeHolder.registerRequestHandler(REQUEST_GET_AGE, new RequestHandlerConverter<>(String.class, Integer.class, handler));
    }

    @Override
    public void getPerson(@NonNull final ElectrodeBridgeResponseListener<Person> response) {
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(REQUEST_GET_PERSON)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.JS)
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onSuccess(Bundle bundle) {
                Person payload = new Person(bundle);
                response.onSuccess(payload);
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                response.onFailure(failureMessage);
            }
        });
    }


    @Override
    public void getStatus(@NonNull Person person, @NonNull final ElectrodeBridgeResponseListener<Status> response) {
        Bundle bundle = person.toBundle();
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(REQUEST_GET_STATUS)
                .withData(bundle)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.NATIVE)
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {

            @Override
            public void onSuccess(Bundle bundle) {
                Status payload = Status.fromBundle(bundle);
                response.onSuccess(payload);
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                response.onFailure(failureMessage);
            }
        });
    }


    @Override
    public void getUserName(@NonNull final ElectrodeBridgeResponseListener<String> response) {
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(REQUEST_GET_USER_NAME)
                .withData(Bundle.EMPTY)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.JS)
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {

            @Override
            public void onSuccess(Bundle bundle) {
                response.onSuccess(bundle.getString("userName"));
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                response.onFailure(failureMessage);
            }

        });
    }

    @Override
    public void getAge(@NonNull String name, @NonNull final ElectrodeBridgeResponseListener<Integer> responseListener) {
        Bundle data = new Bundle();
        data.putString("req", name);
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(REQUEST_GET_AGE)
                .withData(data)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.NATIVE)
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                responseListener.onFailure(failureMessage);
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                responseListener.onSuccess(responseData.getInt("rsp"));
            }
        });
    }
}
