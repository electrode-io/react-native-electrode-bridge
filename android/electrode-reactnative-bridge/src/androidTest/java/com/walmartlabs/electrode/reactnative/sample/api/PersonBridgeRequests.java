package com.walmartlabs.electrode.reactnative.sample.api;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridge;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequest;
import com.walmartlabs.electrode.reactnative.bridge.RequestCompletionListener;
import com.walmartlabs.electrode.reactnative.bridge.RequestDispatcherImpl;
import com.walmartlabs.electrode.reactnative.bridge.helpers.RequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.helpers.RequestHandlerEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Response;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

/***
 * This is a test API generated for testing the bridge. This is first generated using api gen commend and necessary changes are made.
 * <p>
 * This needs to be kept as a reference for generating the api code. This is how a generated API code looks like(format, order, naming conventions etc.)
 * <p>
 * This class provides all the request actions that can be performed on Person.
 */
public final class PersonBridgeRequests {

    private static final String EVENT_GET_PERSON = "com.apisample.ern.apisample.get.person";
    private static final String EVENT_GET_STATUS = "com.apisample.ern.apisample.get.status";

    /***
     * Registers a handler that returns the current user when {@link #getPersonRequest(Response)} is invoked through client(Native or JS side).
     *
     * @param handler {@link RequestDispatcherImpl.RequestHandler}
     */
    public static void registerGetPersonRequestHandler(@NonNull final RequestHandler<Person> handler) {
        ElectrodeBridge.registerRequestHandler(EVENT_GET_PERSON, new RequestDispatcherImpl.RequestHandler() {
            @Override
            public void onRequest(Bundle bundle, final RequestDispatcherImpl.RequestCompletioner requestCompletioner) {
                handler.handleRequest(new Response<Person>() {
                    @Override
                    public void onSuccess(Person obj) {
                        Bundle bundle = obj.toBundle();
                        requestCompletioner.success(bundle);
                    }

                    @Override
                    public void onError(String code, String message) {
                        requestCompletioner.error(code, message);
                    }
                });
            }
        });
    }

    /**
     * Registers a handler  that returns the user status when {@link #getStatusRequest(Person, Response)} is invoked through client(Native or JS side).
     *
     * @param handler {@link RequestDispatcherImpl.RequestHandler}
     */
    public static void registerGetStatusRequestHandler(@NonNull final RequestHandlerEx<Person, Status> handler) {
        ElectrodeBridge.registerRequestHandler(EVENT_GET_STATUS, new RequestDispatcherImpl.RequestHandler() {
            @Override
            public void onRequest(Bundle bundle, final RequestDispatcherImpl.RequestCompletioner requestCompletioner) {

                Person payload = Person.fromBundle(bundle);

                handler.handleRequest(payload, new Response<Status>() {
                    @Override
                    public void onSuccess(Status obj) {
                        Bundle bundle = obj.toBundle();
                        requestCompletioner.success(bundle);
                    }

                    @Override
                    public void onError(String code, String message) {
                        requestCompletioner.error(code, message);
                    }
                });
            }
        });
    }


    /**
     * Calling this method will trigger a bridge request to call the registered handler for a response.
     * <p>
     * The response will be issued via provided {@link Response<Person>#onSuccess({@link Person})} or {@link Response#onError(String, String)} call backs based on the result.
     *
     * @param response {@link Response<Person>} Request listener as a call back to be called once the operation is completed.
     */
    public static void getPersonRequest(@NonNull final Response<Person> response) {
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(EVENT_GET_PERSON)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.JS)
                .build();

        ElectrodeBridge.sendRequest(req, new RequestCompletionListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                Person payload = Person.fromBundle(bundle);
                response.onSuccess(payload);
            }

            @Override
            public void onError(String code, String message) {
                response.onError(code, message);
            }
        });
    }

    /**
     * Calling this method will trigger a bridge request to call the registered handler for a response.
     * <p>
     * The response will be issued via provided {@link Response<Status>#onSuccess({@link Status})} or {@link Response#onError(String, String)} call backs based on the result.
     *
     * @param response {@link Response<Status>} Request listener as a call back to be called once the operation is completed.
     */
    public static void getStatusRequest(@NonNull Person person, @NonNull final Response<Status> response) {
        Bundle bundle = person.toBundle();
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(EVENT_GET_STATUS)
                .withData(bundle)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.JS)
                .build();

        ElectrodeBridge.sendRequest(req, new RequestCompletionListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                Status payload = Status.fromBundle(bundle);
                response.onSuccess(payload);
            }

            @Override
            public void onError(String code, String message) {
                response.onError(code, message);
            }
        });
    }
}
