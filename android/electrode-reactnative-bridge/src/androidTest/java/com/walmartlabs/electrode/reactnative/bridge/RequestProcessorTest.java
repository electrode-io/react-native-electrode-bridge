package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.PromiseImpl;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;
import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nonnull;

import static com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeInternal.BRIDGE_MSG_DATA;

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
            public void onEvent(@NonNull String eventName, @Nullable WritableMap message, @NonNull BaseBridgeTestCase.MockJsResponseDispatcher jsResponseDispatcher) {
                assertEquals(PersonApi.Requests.REQUEST_GET_USER_NAME, eventName);
                assertNotNull(message);
                WritableMap response = Arguments.createMap();
                response.putString(BRIDGE_MSG_DATA, expectedResult);
                jsResponseDispatcher.dispatchResponse(response);
                countDownLatch.countDown();
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
            public void onEvent(@NonNull String eventName, @Nullable WritableMap message, @NonNull BaseBridgeTestCase.MockJsResponseDispatcher jsResponseDispatcher) {
                assertEquals(PersonApi.Requests.REQUEST_GET_STATUS, eventName);
                assertNotNull(message);
                Bundle personBundle = BridgeArguments.responseBundle(message, ElectrodeBridgeInternal.BRIDGE_MSG_DATA);
                assertNotNull(personBundle);
                Person person = BridgeArguments.generateObject(personBundle, Person.class, BridgeArguments.Type.RESPONSE);
                assertNotNull(person);
                assertEquals(actualPerson.getName(), person.getName());
                assertEquals(actualPerson.getMonth(), person.getMonth());

                WritableMap statusMap = Arguments.createMap();
                statusMap.putBoolean("member", result.getMember());
                statusMap.putBoolean("log", result.getLog());

                WritableMap result = Arguments.createMap();
                result.putMap(BRIDGE_MSG_DATA, statusMap);
                jsResponseDispatcher.dispatchResponse(result);
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

        WritableMap request = Arguments.createMap();
        WritableMap inputPerson = Arguments.createMap();
        inputPerson.putString("name", person.getName());
        inputPerson.putInt("month", person.getMonth());
        request.putMap(BridgeArguments.Type.REQUEST.getKey(), inputPerson);

        ElectrodeBridgeInternal.instance().dispatchRequest(PersonApi.Requests.REQUEST_GET_STATUS, "dummy.id.get.user.status", request, new PromiseImpl(new Callback() {
            @Override
            public void invoke(Object... args) {
                assertNotNull(args);
                assertTrue(args.length == 1);
                Object actualResult = args[0];
                assertTrue(actualResult instanceof ReadableMap);
                ReadableMap response = ((ReadableMap) actualResult).getMap("rsp");
                assertSame(result.getMember(), response.getBoolean("member"));
                assertSame(result.getLog(), response.getBoolean("log"));
            }
        }, new Callback() {
            @Override
            public void invoke(Object... args) {
                fail();
            }
        }));

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

}