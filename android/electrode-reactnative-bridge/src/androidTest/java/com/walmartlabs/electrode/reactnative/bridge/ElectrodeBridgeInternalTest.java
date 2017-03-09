package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;
import com.walmartlabs.electrode.reactnative.sample.model.Person;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.walmartlabs.electrode.reactnative.bridge.BridgeMessage.BRIDGE_MSG_DATA;

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

        electrodeBridge.registerRequestHandler("sampleRequest", new ElectrodeBridgeRequestHandler<Bundle, Object>() {
            @Override
            public void onRequest(@Nullable Bundle payload, @NonNull ElectrodeBridgeResponseListener<Object> responseListener) {
                assertNotNull(payload);
                assertNull(payload.get(BRIDGE_MSG_DATA));
                assertNotNull(responseListener);
                responseListener.onSuccess(expectedResult);
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
                assertEquals(expectedResult, responseData.getString(BRIDGE_MSG_DATA));
                countDownLatch.countDown();
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testSendRequestWithRequestDataAndEmptyResponse() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final String expectedInput = "expectedInput";
        ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();

        electrodeBridge.registerRequestHandler("sampleRequest", new ElectrodeBridgeRequestHandler<Bundle, Object>() {
            @Override
            public void onRequest(@Nullable Bundle payload, @NonNull ElectrodeBridgeResponseListener<Object> responseListener) {
                assertNotNull(payload);
                assertEquals(expectedInput, payload.getString(BRIDGE_MSG_DATA));
                assertNotNull(responseListener);
                responseListener.onSuccess(null);
                countDownLatch.countDown();
            }
        });


        ElectrodeBridgeRequest electrodeBridgeRequest = new ElectrodeBridgeRequest.Builder("sampleRequest").withData(expectedInput).build();
        electrodeBridge.sendRequest(electrodeBridgeRequest, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                assertNotNull(responseData);
                assertNull(responseData.get(BRIDGE_MSG_DATA));
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
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(request);
                assertEquals(REQUEST_NAME, request.getString(ElectrodeBridgeRequest.BRIDGE_MSG_NAME));
                assertNotNull(jsResponseDispatcher);
                assertEquals(expectedInput, request.getString(BRIDGE_MSG_DATA));
                jsResponseDispatcher.dispatchResponse(Arguments.createMap());
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(ReadableMap response) {
                fail();
            }

            @Override
            public void onEvent(ReadableMap event) {
                fail();
            }
        });

        ElectrodeBridgeRequest electrodeBridgeRequest = new ElectrodeBridgeRequest.Builder(REQUEST_NAME).withData(expectedInput).build();
        electrodeBridge.sendRequest(electrodeBridgeRequest, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                assertNotNull(responseData);
                assertNotNull(responseData.get(BRIDGE_MSG_DATA));
                assertTrue(responseData.getBundle(BRIDGE_MSG_DATA).isEmpty());
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }


    public void testEmitEventWithSimpleDataFromNative() {
        final String TEST_EVENT_NAME = "testEmitEventWithData";
        final String TEST_EVENT_VALUE = "this is a test event";

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();
        electrodeBridge.addEventListener(TEST_EVENT_NAME, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                assertNotNull(eventPayload);
                assertTrue(eventPayload.containsKey(BRIDGE_MSG_DATA));
                assertEquals(TEST_EVENT_VALUE, eventPayload.getString(BRIDGE_MSG_DATA));
                countDownLatch.countDown();
            }
        });

        UUID uuid = addMockEventListener(TEST_EVENT_NAME, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                fail();
            }

            @Override
            public void onEvent(ReadableMap event) {
                assertNotNull(event);
                assertEquals(TEST_EVENT_NAME, event.getString(ElectrodeBridgeEvent.BRIDGE_MSG_NAME));
                assertTrue(event.hasKey(BRIDGE_MSG_DATA));
                assertEquals(TEST_EVENT_VALUE, event.getString(BRIDGE_MSG_DATA));
                countDownLatch.countDown();
            }
        });

        electrodeBridge.emitEvent(new ElectrodeBridgeEvent.Builder(TEST_EVENT_NAME).withData(TEST_EVENT_VALUE).build());
        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testEmitEventWithSimpleDataFromJS() {
        final String TEST_EVENT_NAME = "testEmitEventWithSimpleDataFromJS";
        final String TEST_EVENT_VALUE = "this is a test event";
        final WritableMap eventMap = createTestEventMap(TEST_EVENT_NAME);
        eventMap.putString(BRIDGE_MSG_DATA, TEST_EVENT_VALUE);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();
        electrodeBridge.addEventListener(TEST_EVENT_NAME, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                assertNotNull(eventPayload);
                assertTrue(eventPayload.containsKey(BRIDGE_MSG_DATA));
                assertEquals(TEST_EVENT_VALUE, eventPayload.getString(BRIDGE_MSG_DATA));
                countDownLatch.countDown();
            }
        });


        ElectrodeBridgeInternal.instance().dispatchEvent(eventMap);

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testEmitEventWithComplexDataFromNative() {
        final String TEST_EVENT_NAME = "testEmitEventWithComplexDataFromNative";
        final Person person = new Person.Builder("Richard Lemaire", 10).build();

        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();
        electrodeBridge.addEventListener(TEST_EVENT_NAME, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                assertNotNull(eventPayload);
                Bundle resultBundle = eventPayload.getBundle(BRIDGE_MSG_DATA);
                assertNotNull(resultBundle);
                assertEquals(person.getName(), resultBundle.getString("name"));
                assertEquals(person.getMonth(), BridgeArguments.getNumberValue(resultBundle, "month"));
                countDownLatch.countDown();
            }
        });

        UUID uuid = addMockEventListener(TEST_EVENT_NAME, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                fail();
            }

            @Override
            public void onEvent(ReadableMap event) {
                assertNotNull(event);
                assertEquals(TEST_EVENT_NAME, event.getString(ElectrodeBridgeEvent.BRIDGE_MSG_NAME));
                assertTrue(event.hasKey(BRIDGE_MSG_DATA));
                ReadableMap personMap = event.getMap(BRIDGE_MSG_DATA);
                assertEquals(person.getName(), personMap.getString("name"));
                assertEquals(person.getMonth(), Integer.valueOf((int) personMap.getDouble("month")));
                countDownLatch.countDown();
            }
        });

        electrodeBridge.emitEvent(new ElectrodeBridgeEvent.Builder(TEST_EVENT_NAME).withData(person).build());

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testEmitEventWithComplexDataFromJS() {
        final String TEST_EVENT_NAME = "testEmitEventWithComplexDataFromJS";
        final Person person = new Person.Builder("Richard Lemaire", 10).build();
        final WritableMap eventMap = createTestEventMap(TEST_EVENT_NAME);
        eventMap.putMap(BRIDGE_MSG_DATA, Arguments.fromBundle(person.toBundle()));

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final ElectrodeBridge electrodeBridge = ElectrodeBridgeInternal.instance();
        electrodeBridge.addEventListener(TEST_EVENT_NAME, new ElectrodeBridgeEventListener<Bundle>() {
            @Override
            public void onEvent(@Nullable Bundle eventPayload) {
                assertNotNull(eventPayload);
                Bundle eventData = eventPayload.getBundle(BRIDGE_MSG_DATA);
                assertNotNull(eventData);
                assertNotNull(eventData.getString("name"));
                assertEquals(person.getName(), eventData.getString("name"));
                countDownLatch.countDown();
            }
        });

        ElectrodeBridgeInternal.instance().dispatchEvent(eventMap);

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testEmitEventWithNoData() {

    }
}