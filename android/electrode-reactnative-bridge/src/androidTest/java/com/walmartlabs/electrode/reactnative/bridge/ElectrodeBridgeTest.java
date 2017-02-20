package com.walmartlabs.electrode.reactnative.bridge;

import com.walmartlabs.electrode.reactnative.bridge.helpers.RequestHandlerEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Response;
import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.concurrent.CountDownLatch;

public class ElectrodeBridgeTest extends BaseBridgeTestCase {

    public void testSampleRequest() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PersonApi.requests().getUserName(new Response<String>() {
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
        final Status result = new Status.Builder(true).log(true).build();
        final Person person = new Person.Builder("John", 05).build();

        PersonApi.requests().registerGetStatusRequestHandler(new RequestHandlerEx<Person, Status>() {
            @Override
            public void handleRequest(Person payload, Response<Status> response) {
                assertEquals(person.getName(), payload.getName());
                assertEquals(person.getMonth(), payload.getMonth());
                response.onSuccess(result);
            }
        });


        PersonApi.requests().getStatus(person, new Response<Status>() {
            @Override
            public void onSuccess(Status obj) {
                assertNotNull(obj);
                assertEquals(result.getLog(), obj.getLog());
                assertEquals(result.getMember(), obj.getMember());
                countDownLatch.countDown();
            }

            @Override
            public void onError(String code, String message) {
                fail();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }
}