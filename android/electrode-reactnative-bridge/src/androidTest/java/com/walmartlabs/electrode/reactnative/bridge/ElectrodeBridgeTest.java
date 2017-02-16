package com.walmartlabs.electrode.reactnative.bridge;

import com.walmartlabs.electrode.reactnative.bridge.helpers.RequestHandlerEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Response;
import com.walmartlabs.electrode.reactnative.sample.api.PersonBridgeRequests;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.concurrent.CountDownLatch;

/**
 * Created by d0g00g4 on 2/13/17.
 */
public class ElectrodeBridgeTest extends BaseBridgeTestCase {

    public void testSampleRequest() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PersonBridgeRequests.getUserNameRequest(new Response<String>() {
            @Override
            public void onSuccess(String obj) {
                fail();
            }

            @Override
            public void onError(String code, String message) {
                assertNotNull(code);
                assertNotNull(message);
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }


    public void testRegisterGetStatusRequestHandler() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);


        PersonBridgeRequests.registerGetStatusRequestHandler(new RequestHandlerEx<Person, Status>() {
            @Override
            public void handleRequest(Person payload, Response<Status> response) {
                response.onSuccess(new Status.Builder(true).log(true).build());
            }
        });


        Person person = new Person.Builder("John", 05).build();
        PersonBridgeRequests.getStatusRequest(person, new Response<Status>() {
            @Override
            public void onSuccess(Status obj) {
                fail();
            }

            @Override
            public void onError(String code, String message) {
                fail();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }
}