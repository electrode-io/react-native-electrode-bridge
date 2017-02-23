package com.walmartlabs.electrode.reactnative.sample.api;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEventListener;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeHolder;
import com.walmartlabs.electrode.reactnative.bridge.EventListenerProcessor;
import com.walmartlabs.electrode.reactnative.bridge.EventProcessor;
import com.walmartlabs.electrode.reactnative.sample.model.Person;

/**
 * Class that holds all event actions that can be performed on Person module.
 */
final class PersonEvents implements PersonApi.Events {

    PersonEvents() {

    }

    @Override
    public void addPersonAddedEventListener(@NonNull final ElectrodeBridgeEventListener<Person> eventListener) {
        ElectrodeBridgeHolder.addEventListener(EVENT_PERSON_ADDED, new EventListenerProcessor<>(Person.class, eventListener));
    }

    @Override
    public void addPersonNameUpdatedEventListener(@NonNull final ElectrodeBridgeEventListener<String> eventListener) {
        ElectrodeBridgeHolder.addEventListener(EVENT_PERSON_NAME_UPDATED, new EventListenerProcessor<>(String.class, eventListener));
    }


    @Override
    public void emitEventPersonAdded(@NonNull Person person) {
        new EventProcessor<>(EVENT_PERSON_ADDED, person).execute();
    }

    @Override
    public void emitEventPersonNameUpdated(@NonNull String updatedName) {
        new EventProcessor<>(EVENT_PERSON_NAME_UPDATED, updatedName).execute();
    }
}
