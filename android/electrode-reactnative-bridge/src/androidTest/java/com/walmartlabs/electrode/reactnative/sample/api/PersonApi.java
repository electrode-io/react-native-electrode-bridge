package com.walmartlabs.electrode.reactnative.sample.api;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.helpers.EventListener;
import com.walmartlabs.electrode.reactnative.bridge.helpers.RequestHandler;
import com.walmartlabs.electrode.reactnative.bridge.helpers.RequestHandlerEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Response;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

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

        String REQUEST_PERSON_ADDED = "com.apisample.ern.apisample.person.added";
        String REQUEST_PERSON_NAME_UPDATED = "com.apisample.ern.apisample.person.name.updated";

        /**
         * Registers an event listener to the bridge for event{@link #REQUEST_PERSON_ADDED}. Event listener's {@link EventListener#onEvent(Object)} will be invoked when the event happens.
         *
         * @param eventListener {@link EventListener}
         */
        void addPersonAddedEventListener(@NonNull final EventListener<Person> eventListener);

        /**
         * Registers an event listener to the bridge for event{@link #REQUEST_PERSON_NAME_UPDATED}. Event listener's {@link EventListener#onEvent(Object)} will be invoked when the event happens.
         *
         * @param eventListener {@link EventListener}
         */
        void addPersonNameUpdatedEventListener(@NonNull final EventListener<String> eventListener);

        /**
         * Calling this method will emit an event {@link #REQUEST_PERSON_ADDED} to all the registered {@link com.walmartlabs.electrode.reactnative.bridge.EventDispatcherImpl.EventListener}(s).
         *
         * @param person {@link Person}
         */
        void emitEventPersonAdded(@NonNull Person person);

        /**
         * Calling this method will emit an event {@link #REQUEST_PERSON_NAME_UPDATED} to all the registered {@link com.walmartlabs.electrode.reactnative.bridge.EventDispatcherImpl.EventListener}(s).
         *
         * @param updatedName {@link String}
         */
        void emitEventPersonNameUpdated(@NonNull String updatedName);
    }

    public interface Requests {

        final String EVENT_GET_PERSON = "com.apisample.ern.apisample.get.person";
        final String EVENT_GET_STATUS = "com.apisample.ern.apisample.get.status";
        final String EVENT_GET_USER_NAME = "com.apisample.ern.apisample.get.user.name";

        /***
         * Registers a handler that returns the current user when {@link #getPerson(Response)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        void registerGetPersonRequestHandler(@NonNull final RequestHandler<Person> handler);

        /**
         * Registers a handler  that returns the user status when {@link #getStatus(Person, Response)} is invoked through client(Native or JS side).
         *
         * @param handler {@link ElectrodeBridgeRequestHandler}
         */
        void registerGetStatusRequestHandler(@NonNull final RequestHandlerEx<Person, Status> handler);


        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link Response<Person>#onSuccess({@link Person})} or {@link Response#onError(String, String)} call backs based on the result.
         *
         * @param response {@link Response<Person>} Request listener as a call back to be called once the operation is completed.
         */
        void getPerson(@NonNull final Response<Person> response);

        /**
         * Calling this method will trigger a bridge request to call the registered handler for a response.
         * <p>
         * The response will be issued via provided {@link Response<Status>#onSuccess({@link Status})} or {@link Response#onError(String, String)} call backs based on the result.
         *
         * @param response {@link Response<Status>} Request listener as a call back to be called once the operation is completed.
         */
        void getStatus(@NonNull Person person, @NonNull final Response<Status> response);


        void getUserName(@NonNull final Response<String> response);

    }

}
