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
        new EventListenerProcessor<>(EVENT_PERSON_ADDED, Person.class, eventListener).execute();
    }

    @Override
    public void addPersonNameUpdatedEventListener(@NonNull final ElectrodeBridgeEventListener<String> eventListener) {
        new EventListenerProcessor<>(EVENT_PERSON_NAME_UPDATED, String.class, eventListener).execute();
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
