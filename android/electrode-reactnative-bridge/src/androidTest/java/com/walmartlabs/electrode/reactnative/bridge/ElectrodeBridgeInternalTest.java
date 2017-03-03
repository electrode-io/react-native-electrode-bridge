package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;
import com.walmartlabs.electrode.reactnative.sample.model.Person;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeInternal.BRIDGE_MSG_DATA;

public class ElectrodeBridgeInternalTest extends BaseBridgeTestCase {

    public void testSendRequestForTimeOut() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();

        ElectrodeBridgeRequest electrodeBridgeRequest = new ElectrodeBridgeRequest.Builder("sampleRequest").build();

        electrodeBridge.sendRequest(electrodeBridgeRequest, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                assertNotNull(failureMessage);
                countDownLatch.countDown();
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                fail();
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testSendRequestWithEmptyRequestDataAndNonEmptyResponse() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final String expectedResult = "yay tests";
        ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();

        electrodeBridge.registerRequestHandler("sampleRequest", new ElectrodeBridgeRequestHandler<Bundle, Bundle>() {
            @Override
            public void onRequest(@Nullable Bundle payload, @NonNull ElectrodeBridgeResponseListener<Bundle> responseListener) {
                assertNotNull(payload);
                assertTrue(payload.isEmpty());
                assertNotNull(responseListener);
                responseListener.onSuccess(BridgeArguments.generateBundle(expectedResult, BridgeArguments.Type.RESPONSE));
                countDownLatch.countDown();
            }
        });


        ElectrodeBridgeRequest electrodeBridgeRequest = new ElectrodeBridgeRequest.Builder("sampleRequest").build();
        electrodeBridge.sendRequest(electrodeBridgeRequest, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                assertNotNull(responseData);
                assertEquals(expectedResult, responseData.getString("rsp"));
                countDownLatch.countDown();
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testSendRequestWithRequestDataAndEmptyResponse() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final String expectedInput = "expectedInput";
        ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();

        electrodeBridge.registerRequestHandler("sampleRequest", new ElectrodeBridgeRequestHandler<Bundle, Bundle>() {
            @Override
            public void onRequest(@Nullable Bundle payload, @NonNull ElectrodeBridgeResponseListener<Bundle> responseListener) {
                assertNotNull(payload);
                assertEquals(expectedInput, payload.getString("req"));
                assertNotNull(responseListener);
                responseListener.onSuccess(null);
                countDownLatch.countDown();
            }
        });


        Bundle bundle = new Bundle();
        bundle.putString("req", expectedInput);
        ElectrodeBridgeRequest electrodeBridgeRequest = new ElectrodeBridgeRequest.Builder("sampleRequest").withData(bundle).build();
        electrodeBridge.sendRequest(electrodeBridgeRequest, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                assertNotNull(responseData);
                assertTrue(responseData.isEmpty());
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testSendRequestWithRequestDataAndEmptyResponseWithJSRequestHandler() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final String expectedInput = "expectedInput";
        final String REQUEST_NAME = "testSendRequestWithRequestDataAndEmptyResponseWithJSRequestHandler";
        ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();

        UUID uuid = addMockEventListener(REQUEST_NAME, new MockElectrodeEventListener() {
            @Override
            public void onEvent(@NonNull String eventName, @Nullable WritableMap message, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertEquals(REQUEST_NAME, eventName);
                assertNotNull(message);
                assertNotNull(jsResponseDispatcher);
                assertEquals(expectedInput, message.getMap("data").getString("req"));
                jsResponseDispatcher.dispatchResponse(Arguments.createMap());
                countDownLatch.countDown();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("req", expectedInput);
        ElectrodeBridgeRequest electrodeBridgeRequest = new ElectrodeBridgeRequest.Builder(REQUEST_NAME).withData(bundle).build();
        electrodeBridge.sendRequest(electrodeBridgeRequest, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                assertNotNull(responseData);
                assertTrue(responseData.isEmpty());
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }


    public void testEmitEventWithData() {
        final String TEST_EVENT_NAME = "testEmitEventWithData";
        final String TEST_EVENT_KEY = BridgeArguments.Type.EVENT.getKey();
        final String TEST_EVENT_VALUE = "this is a test event";
        final Bundle eventBundle = new Bundle();
        eventBundle.putString(TEST_EVENT_KEY, TEST_EVENT_VALUE);

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();
        electrodeBridge.addEventListener(TEST_EVENT_NAME, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                assertNotNull(eventPayload);
                assertTrue(eventPayload.containsKey(TEST_EVENT_KEY));
                assertEquals(TEST_EVENT_VALUE, eventPayload.getString(TEST_EVENT_KEY));
                countDownLatch.countDown();
            }
        });

        UUID uuid = addMockEventListener(TEST_EVENT_NAME, new MockElectrodeEventListener() {
            @Override
            public void onEvent(@NonNull String eventName, @Nullable WritableMap message, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertEquals(TEST_EVENT_NAME, eventName);
                assertNotNull(message);
                assertTrue(message.hasKey(BRIDGE_MSG_DATA));
                assertEquals(TEST_EVENT_VALUE, message.getMap(BRIDGE_MSG_DATA).getString(TEST_EVENT_KEY));
                countDownLatch.countDown();
            }
        });

        electrodeBridge.emitEvent(new ElectrodeBridgeEvent.Builder(TEST_EVENT_NAME).withData(eventBundle).build());
        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testEmitEventWithComplexData() {
        final String TEST_EVENT_NAME = "testEmitEvent";
        final Person person = new Person.Builder("Richard Lemaire", 10).build();
        final Bundle eventBundle = person.toBundle();

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();
        electrodeBridge.addEventListener(TEST_EVENT_NAME, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                assertNotNull(eventPayload);
                assertNotNull(eventPayload.getString("name"));
                assertEquals(person.getName(), eventPayload.getString("name"));
                assertEquals(person.getMonth(), BridgeArguments.getNumberValue(eventBundle, "month"));
                countDownLatch.countDown();
            }
        });

        UUID uuid = addMockEventListener(TEST_EVENT_NAME, new MockElectrodeEventListener() {
            @Override
            public void onEvent(@NonNull String eventName, @Nullable WritableMap message, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertEquals(TEST_EVENT_NAME, eventName);
                assertNotNull(message);
                assertTrue(message.hasKey(BRIDGE_MSG_DATA));
                assertEquals(person.getName(), message.getMap(BRIDGE_MSG_DATA).getString("name"));
                assertEquals(person.getMonth(), Integer.valueOf((int) message.getMap(BRIDGE_MSG_DATA).getDouble("month")));
                countDownLatch.countDown();
            }
        });

        electrodeBridge.emitEvent(new ElectrodeBridgeEvent.Builder(TEST_EVENT_NAME).withData(eventBundle).build());

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testEmitEventWithNoData() {

    }
}