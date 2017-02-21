package com.walmartlabs.electrode.reactnative.sample.api;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEvent;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeEventListener;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeHolder;
import com.walmartlabs.electrode.reactnative.sample.model.Person;

/**
 * Class that holds all event actions that can be performed on Person module.
 */
final class PersonEvents implements PersonApi.Events {

    PersonEvents() {

    }

    @Override
    public void addPersonAddedEventListener(@NonNull final ElectrodeBridgeEventListener<Person> eventListener) {
        ElectrodeBridgeHolder.registerEventListener(REQUEST_PERSON_ADDED, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@NonNull Bundle bundle) {
                Person payload = Person.fromBundle(bundle);
                eventListener.onEvent(payload);
            }
        });
    }

    @Override
    public void addPersonNameUpdatedEventListener(@NonNull final ElectrodeBridgeEventListener<String> eventListener) {
        ElectrodeBridgeHolder.registerEventListener(REQUEST_PERSON_NAME_UPDATED, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@NonNull Bundle bundle) {
                String payload = bundle.getString("personNameUpdated");
                eventListener.onEvent(payload);
            }
        });
    }


    @Override
    public void emitEventPersonAdded(@NonNull Person person) {
        Bundle bundle = person.toBundle();
        ElectrodeBridgeHolder.emitEvent(new ElectrodeBridgeEvent.Builder(REQUEST_PERSON_ADDED)
                .withDispatchMode(ElectrodeBridgeEvent.DispatchMode.JS)
                .withData(bundle)
                .build());
    }

    @Override
    public void emitEventPersonNameUpdated(@NonNull String updatedName) {
        Bundle bundle = new Bundle();
        bundle.putString("updatedName", updatedName);
        ElectrodeBridgeHolder.emitEvent(new ElectrodeBridgeEvent.Builder(REQUEST_PERSON_NAME_UPDATED)
                .withDispatchMode(ElectrodeBridgeEvent.DispatchMode.JS)
                .withData(bundle)
                .build());
    }
}
