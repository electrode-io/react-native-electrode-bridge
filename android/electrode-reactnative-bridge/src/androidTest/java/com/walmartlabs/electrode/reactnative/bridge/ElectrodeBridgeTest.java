package com.walmartlabs.electrode.reactnative.bridge;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Response;
import com.walmartlabs.electrode.reactnative.sample.api.PersonBridgeRequests;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by d0g00g4 on 2/13/17.
 */
public class ElectrodeBridgeTest extends BaseBridgeTestCase {


    public void testSample() {
        final CountDownLatch countDown = new CountDownLatch(1);

        Person person = new Person.Builder("John", 05).build();
        PersonBridgeRequests.getStatusRequest(person, new Response<Status>() {
            @Override
            public void onSuccess(Status obj) {
                fail();
            }

            @Override
            public void onError(String code, String message) {
                assertNotNull(code);
                assertNotNull(message);
                countDown.countDown();
            }
        });

        try {
            assertTrue(countDown.await(10, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail();
        }
    }
}