package com.walmartlabs.electrode.reactnative.sample.api;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridge;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEvent;
import com.walmartlabs.electrode.reactnative.bridge.EventDispatcherImpl;
import com.walmartlabs.electrode.reactnative.bridge.helpers.EventListener;
import com.walmartlabs.electrode.reactnative.sample.model.Person;

/**
 * Class that holds all event actions that can be performed on Person module.
 */
public final class PersonBridgeEvents {
    private static final String REQUEST_PERSON_ADDED = "com.apisample.ern.apisample.person.added";
    private static final String REQUEST_PERSON_NAME_UPDATED = "com.apisample.ern.apisample.person.name.updated";

    /**
     * Registers an event listener to the bridge for event{@link #REQUEST_PERSON_ADDED}. Event listener's {@link EventListener#onEvent(Object)} will be invoked when the event happens.
     *
     * @param eventListener {@link EventListener}
     */
    public static final void registerPersonAddedEventListener(@NonNull final EventListener<Person> eventListener) {
        ElectrodeBridge.registerEventListener(REQUEST_PERSON_ADDED, new EventDispatcherImpl.EventListener() {
            @Override
            public void onEvent(@NonNull Bundle bundle) {
                Person payload = Person.fromBundle(bundle);
                eventListener.onEvent(payload);
            }
        });
    }

    /**
     * Registers an event listener to the bridge for event{@link #REQUEST_PERSON_NAME_UPDATED}. Event listener's {@link EventListener#onEvent(Object)} will be invoked when the event happens.
     *
     * @param eventListener {@link EventListener}
     */
    public static final void registerPersonNameUpdatedEventListener(@NonNull final EventListener<String> eventListener) {
        ElectrodeBridge.registerEventListener(REQUEST_PERSON_NAME_UPDATED, new EventDispatcherImpl.EventListener() {
            @Override
            public void onEvent(@NonNull Bundle bundle) {
                String payload = bundle.getString("personNameUpdated");
                eventListener.onEvent(payload);
            }
        });
    }

    /**
     * Calling this method will emit an event {@link #REQUEST_PERSON_ADDED} to all the registered {@link com.walmartlabs.electrode.reactnative.bridge.EventDispatcherImpl.EventListener}(s).
     *
     * @param person {@link Person}
     */
    public static void emitEventPersonAdded(@NonNull Person person) {
        Bundle bundle = person.toBundle();
        ElectrodeBridge.emitEvent(new ElectrodeBridgeEvent.Builder(REQUEST_PERSON_ADDED)
                .withDispatchMode(ElectrodeBridgeEvent.DispatchMode.JS)
                .withData(bundle)
                .build());
    }

    /**
     * Calling this method will emit an event {@link #REQUEST_PERSON_NAME_UPDATED} to all the registered {@link com.walmartlabs.electrode.reactnative.bridge.EventDispatcherImpl.EventListener}(s).
     *
     * @param updatedName {@link String}
     */
    public static void emitEventPersonNameUpdated(@NonNull String updatedName) {
        Bundle bundle = new Bundle();
        bundle.putString("updatedName", updatedName);
        ElectrodeBridge.emitEvent(new ElectrodeBridgeEvent.Builder(REQUEST_PERSON_NAME_UPDATED)
                .withDispatchMode(ElectrodeBridgeEvent.DispatchMode.JS)
                .withData(bundle)
                .build());
    }
}
