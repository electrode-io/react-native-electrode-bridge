package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.fail;

public class RequestHandleTests extends BaseBridgeTestCase {

    @Test
    public void testRequestHandlerRemoval() {
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        final String requestName = "testRequestHandlerRemoval";

        RequestHandlerHandle requestHandle = new RequestHandlerProcessor<>(requestName, None.class, None.class, new ElectrodeBridgeRequestHandler<None, None>() {
            @Override
            public void onRequest(@Nullable None payload, @NonNull ElectrodeBridgeResponseListener<None> responseListener) {
                assertSame(None.NONE, payload);
                responseListener.onSuccess(null);
                countDownLatch.countDown();
            }
        }).execute();


        new RequestProcessor<None, None>(requestName, null, None.class, new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable None responseData) {
                assertSame(None.NONE, responseData);
                countDownLatch.countDown();
            }
        }).execute();

        requestHandle.unregister();

        new RequestProcessor<None, None>(requestName, null, None.class, new ElectrodeBridgeResponseListener<None>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                failureMessage.getMessage();
                countDownLatch.countDown();
            }

            @Override
            public void onSuccess(@Nullable None responseData) {
                fail();
            }
        }).execute();

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    @Test
    public void testRequestHandlerRemovalWithApi() {
        final CountDownLatch countDownLatch = new CountDownLatch(3);
        final int RESULT_AGE = 10;

        RequestHandlerHandle handlerHandle = PersonApi.requests().registerGetAgeRequestHandler(new ElectrodeBridgeRequestHandler<String, Integer>() {
            @Override
            public void onRequest(@Nullable String payload, @NonNull ElectrodeBridgeResponseListener<Integer> responseListener) {
                responseListener.onSuccess(RESULT_AGE);
                countDownLatch.countDown();
            }
        });

        PersonApi.requests().getAge("user", new ElectrodeBridgeResponseListener<Integer>() {
            @Override
            public void onSuccess(@Nullable Integer responseData) {
                assertNotNull(responseData);
                assertEquals(RESULT_AGE, responseData.intValue());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });

        handlerHandle.unregister();

        PersonApi.requests().getAge("user", new ElectrodeBridgeResponseListener<Integer>() {
            @Override
            public void onSuccess(@Nullable Integer responseData) {
                fail();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                assertNotNull(failureMessage);
                countDownLatch.countDown();
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
    }
}
