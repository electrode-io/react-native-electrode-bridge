package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Position;

import java.util.concurrent.CountDownLatch;

public class EventProcessorTest extends BaseBridgeTestCase {

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