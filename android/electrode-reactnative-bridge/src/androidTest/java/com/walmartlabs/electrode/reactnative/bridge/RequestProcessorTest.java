package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nonnull;

public class RequestProcessorTest extends BaseBridgeTestCase {
    public void testSampleRequestNativeToNativeFailure() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        PersonApi.requests().getUserName(new ElectrodeBridgeResponseListener<String>() {
            @Override
            public void onSuccess(String obj) {
                fail();
            }

            @Override
            public void onFailure(@Nonnull FailureMessage failureMessage) {
                assertNotNull(failureMessage);
                assertNotNull(failureMessage.getCode());
                assertNotNull(failureMessage.getMessage());
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testSampleRequestNativeToJSSuccess() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final String expectedResult = "Richard Mercille";

        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_GET_USER_NAME, new BaseBridgeTestCase.MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(request);
                assertEquals(PersonApi.Requests.REQUEST_GET_USER_NAME, request.getString(ElectrodeBridgeRequest.BRIDGE_MSG_NAME));
                WritableMap response = Arguments.createMap();
                response.putString(ElectrodeBridgeResponse.BRIDGE_MSG_DATA, expectedResult);
                jsResponseDispatcher.dispatchResponse(response);
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

        PersonApi.requests().getUserName(new ElectrodeBridgeResponseListener<String>() {
            @Override
            public void onSuccess(String obj) {
                assertNotNull(expectedResult, obj);
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@Nonnull FailureMessage failureMessage) {
                fail();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);

    }


    public void testRegisterGetStatusRequestHandlerNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Status result = new Status.Builder(true).log(true).build();
        final Person person = new Person.Builder("John", 05).build();

        PersonApi.requests().registerGetStatusRequestHandler(new ElectrodeBridgeRequestHandler<Person, Status>() {
            @Override
            public void onRequest(@Nullable Person payload, @NonNull ElectrodeBridgeResponseListener<Status> responseListener) {
                assertEquals(person.getName(), payload.getName());
                assertEquals(person.getMonth(), payload.getMonth());
                responseListener.onSuccess(result);
            }
        });


        PersonApi.requests().getStatus(person, new ElectrodeBridgeResponseListener<Status>() {
            @Override
            public void onSuccess(Status obj) {
                assertNotNull(obj);
                assertEquals(result.getLog(), obj.getLog());
                assertEquals(result.getMember(), obj.getMember());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@Nonnull FailureMessage failureMessage) {
                fail();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testGetStatusRequestHandlerNativeToJSSuccess() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Person actualPerson = new Person.Builder("John", 05).build();
        final Status result = new Status.Builder(true).log(true).build();

        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_GET_STATUS, new BaseBridgeTestCase.MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(request);
                assertEquals(PersonApi.Requests.REQUEST_GET_STATUS, request.getString(ElectrodeBridgeRequest.BRIDGE_MSG_NAME));

                ReadableMap personMap = request.getMap(BridgeMessage.BRIDGE_MSG_DATA);
                assertNotNull(personMap);

                Bundle personBundle = Arguments.toBundle(personMap);
                Person person = new Person(personBundle);
                assertNotNull(person);
                assertEquals(actualPerson.getName(), person.getName());
                assertEquals(actualPerson.getMonth(), person.getMonth());

                WritableMap statusMap = Arguments.createMap();
                statusMap.putBoolean("member", result.getMember());
                statusMap.putBoolean("log", result.getLog());

                jsResponseDispatcher.dispatchResponse(statusMap);
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


        PersonApi.requests().getStatus(actualPerson, new ElectrodeBridgeResponseListener<Status>() {
            @Override
            public void onSuccess(Status obj) {
                assertNotNull(obj);
                assertEquals(result.getLog(), obj.getLog());
                assertEquals(result.getMember(), obj.getMember());
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(@Nonnull FailureMessage failureMessage) {
                fail();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testGetStatusRequestHandlerJSToNativeSuccess() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Person person = new Person.Builder("John", 05).build();
        final Status result = new Status.Builder(true).log(true).build();
        PersonApi.requests().registerGetStatusRequestHandler(new ElectrodeBridgeRequestHandler<Person, Status>() {
            @Override
            public void onRequest(@Nullable Person payload, @NonNull ElectrodeBridgeResponseListener<Status> responseListener) {
                assertEquals(person.getName(), payload.getName());
                assertEquals(person.getMonth(), payload.getMonth());
                responseListener.onSuccess(result);
            }
        });


        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_GET_STATUS, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                assertNotNull(response);
                ReadableMap responseMap = response.getMap(BridgeMessage.BRIDGE_MSG_DATA);
                assertSame(result.getMember(), responseMap.getBoolean("member"));
                assertSame(result.getLog(), responseMap.getBoolean("log"));
                countDownLatch.countDown();
            }

            @Override
            public void onEvent(ReadableMap event) {
                fail();
            }
        });

        WritableMap inputPerson = Arguments.createMap();
        inputPerson.putString("name", person.getName());
        inputPerson.putInt("month", person.getMonth());

        WritableMap request = Arguments.createMap();
        request.putString(ElectrodeBridgeRequest.BRIDGE_MSG_ID, ElectrodeBridgeRequest.getUUID());
        request.putString(ElectrodeBridgeRequest.BRIDGE_MSG_NAME, PersonApi.Requests.REQUEST_GET_STATUS);
        request.putString(ElectrodeBridgeRequest.BRIDGE_MSG_TYPE, BridgeMessage.Type.REQUEST.getKey());
        request.putMap(ElectrodeBridgeRequest.BRIDGE_MSG_DATA, inputPerson);

        ElectrodeBridgeTransceiver.instance().sendMessage(request);

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);

    }

    public void testPrimitiveTypesForRequestAndResponseNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        PersonApi.requests().registerGetAgeRequestHandler(new ElectrodeBridgeRequestHandler<String, Integer>() {
            @Override
            public void onRequest(@Nullable String payload, @NonNull ElectrodeBridgeResponseListener<Integer> responseListener) {
                assertNotNull(payload);
                assertNotNull(responseListener);
                responseListener.onSuccess(30);
                countDownLatch.countDown();
            }
        });


        PersonApi.requests().getAge("deepu", new ElectrodeBridgeResponseListener<Integer>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Integer responseData) {
                assertNotNull(responseData);
                assertSame(30, responseData);
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testGetPersonRequestSentFromJsWithEmptyDataInRequest() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        ElectrodeBridgeTransceiver electrodeBridgeTransceiver = ElectrodeBridgeTransceiver.instance();

        PersonApi.requests().registerGetPersonRequestHandler(new ElectrodeBridgeRequestHandler<None, Person>() {
            @Override
            public void onRequest(@Nullable None payload, @NonNull ElectrodeBridgeResponseListener<Person> responseListener) {
                assertNull(payload);
                countDownLatch.countDown();
            }
        });


        WritableMap map = Arguments.createMap();
        map.putString(BridgeMessage.BRIDGE_MSG_ID, "492a3aa7-49f6-4a57-929d-757bdf5db49d");
        map.putString(BridgeMessage.BRIDGE_MSG_NAME, PersonApi.Requests.REQUEST_GET_PERSON);
        map.putString(BridgeMessage.BRIDGE_MSG_TYPE, BridgeMessage.Type.REQUEST.getKey());

        electrodeBridgeTransceiver.sendMessage(map);

        waitForCountDownToFinishOrFail(countDownLatch);
    }

}