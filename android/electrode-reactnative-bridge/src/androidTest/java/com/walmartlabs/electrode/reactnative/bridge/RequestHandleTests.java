package com.walmartlabs.electrode.reactnative.bridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;
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

    @Test
    public void testMultipleRequestHandlers() {
        final CountDownLatch countDownLatch = new CountDownLatch(4);
        final int RESULT_AGE_FIRST = 10;
        final int RESULT_AGE_SECOND = 20;
        final int RESULT_AGE_THIRD = 30;

        RequestHandlerHandle handlerHandle = PersonApi.requests().registerGetAgeRequestHandler(new ElectrodeBridgeRequestHandler<String, Integer>() {
            @Override
            public void onRequest(@Nullable String payload, @NonNull ElectrodeBridgeResponseListener<Integer> responseListener) {
                responseListener.onSuccess(RESULT_AGE_FIRST);
                countDownLatch.countDown();
            }
        });

        RequestHandlerHandle handlerHandle1 = PersonApi.requests().registerGetAgeRequestHandler(new ElectrodeBridgeRequestHandler<String, Integer>() {
            @Override
            public void onRequest(@Nullable String payload, @NonNull ElectrodeBridgeResponseListener<Integer> responseListener) {
                responseListener.onSuccess(RESULT_AGE_SECOND);
                countDownLatch.countDown();
            }
        });

        RequestHandlerHandle handlerHandle2 = PersonApi.requests().registerGetAgeRequestHandler(new ElectrodeBridgeRequestHandler<String, Integer>() {
            @Override
            public void onRequest(@Nullable String payload, @NonNull ElectrodeBridgeResponseListener<Integer> responseListener) {
                responseListener.onSuccess(RESULT_AGE_THIRD);
                countDownLatch.countDown();
            }
        });

        PersonApi.requests().getAge("user", new ElectrodeBridgeResponseListener<Integer>() {
            @Override
            public void onSuccess(@Nullable Integer responseData) {
                assertNotNull(responseData);
                assertEquals(RESULT_AGE_THIRD, responseData.intValue());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });

        handlerHandle.unregister();
        handlerHandle1.unregister();

        PersonApi.requests().getAge("user", new ElectrodeBridgeResponseListener<Integer>() {
            @Override
            public void onSuccess(@Nullable Integer responseData) {
                assertNotNull(responseData);
                assertEquals(RESULT_AGE_THIRD, responseData.intValue());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }
        });

        handlerHandle2.unregister();

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

    @Test
    public void testRequestHandlerIsRegisteredOnUnregister() {
        final String requestName = "testRequestHandlerRemoval";

        RequestHandlerHandle requestHandle = new RequestHandlerProcessor<>(requestName, None.class, None.class, new ElectrodeBridgeRequestHandler<None, None>() {
            @Override
            public void onRequest(@Nullable None payload, @NonNull ElectrodeBridgeResponseListener<None> responseListener) {
                assertSame(None.NONE, payload);
                responseListener.onSuccess(null);
            }
        }).execute();
        assertTrue(requestHandle.isRegistered());
        requestHandle.unregister();
        assertFalse(requestHandle.isRegistered());
    }

    @Test
    public void testRequestHandlerIsRegisteredOnReRegister() {
        final String requestName = "testRequestHandlerRemoval";

        RequestHandlerHandle requestHandle = new RequestHandlerProcessor<>(requestName, None.class, None.class, new ElectrodeBridgeRequestHandler<None, None>() {
            @Override
            public void onRequest(@Nullable None payload, @NonNull ElectrodeBridgeResponseListener<None> responseListener) {
                assertSame(None.NONE, payload);
                responseListener.onSuccess(null);
            }
        }).execute();
        assertTrue(requestHandle.isRegistered());
        RequestHandlerHandle newRequestHandle = new RequestHandlerProcessor<>(requestName, None.class, None.class, new ElectrodeBridgeRequestHandler<None, None>() {
            @Override
            public void onRequest(@Nullable None payload, @NonNull ElectrodeBridgeResponseListener<None> responseListener) {
                assertSame(None.NONE, payload);
                responseListener.onSuccess(null);
            }
        }).execute();
        //Ensure a new request handler registration removes the old one
        assertFalse(requestHandle.isRegistered());

        assertTrue(newRequestHandle.isRegistered());
    }
}
