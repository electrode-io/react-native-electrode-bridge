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

package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Position;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class EventProcessorTest extends BaseBridgeTestCase {

    @Test
    public void testEventsForModelObjectNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Position position = new Position.Builder(98.89).lng(89.99).build();
        final Person person = new Person.Builder("chris", 20).position(position).build();
        PersonApi.events().addPersonAddedEventListener(new ElectrodeBridgeEventListener<Person>() {
            @Override
            public void onEvent(@Nullable Person eventPayload) {
                assertNotNull(eventPayload);
                assertEquals(person.getName(), eventPayload.getName());
                assertEquals(person.getMonth(), eventPayload.getMonth());
                assertEquals(person.getPosition().getLng(), eventPayload.getPosition().getLng());
                assertEquals(person.getPosition().getLat(), eventPayload.getPosition().getLat());
                countDownLatch.countDown();
            }
        });
        PersonApi.events().emitEventPersonAdded(person);
        waitForCountDownToFinishOrFail(countDownLatch);
    }

    @Test
    public void testEventsForModelPrimitiveWrapperNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String personName = "chris";
        PersonApi.events().addPersonNameUpdatedEventListener(new ElectrodeBridgeEventListener<String>() {
            @Override
            public void onEvent(@Nullable String eventPayload) {
                assertNotNull(eventPayload);
                assertEquals(personName, eventPayload);
                countDownLatch.countDown();
            }
        });

        PersonApi.events().emitEventPersonNameUpdated(personName);
        waitForCountDownToFinishOrFail(countDownLatch);
    }

}