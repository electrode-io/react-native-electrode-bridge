package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;
import com.walmartlabs.electrode.reactnative.sample.api.PersonApi;
import com.walmartlabs.electrode.reactnative.sample.api.UpdatePersonRequestData;
import com.walmartlabs.electrode.reactnative.sample.model.Person;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.annotation.Nonnull;

import static com.walmartlabs.electrode.reactnative.sample.api.PersonApi.Requests.REQUEST_GET_AGE;

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
        final Person actualPerson = new Person.Builder("John", 05).age(10).build();
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

        WritableMap request = getRequestMap(PersonApi.Requests.REQUEST_GET_STATUS);
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

    public void testIntegerForResponseNativeToJS() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        addMockEventListener(REQUEST_GET_AGE, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(jsResponseDispatcher);
                WritableMap writableMap = Arguments.createMap();
                writableMap.putInt(BridgeMessage.BRIDGE_MSG_DATA, 10);
                jsResponseDispatcher.dispatchResponse(writableMap);
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


        PersonApi.requests().getAge("deepu", new ElectrodeBridgeResponseListener<Integer>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Integer responseData) {
                assertNotNull(responseData);
                assertSame(10, responseData);
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testIntegerForResponseJSToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        PersonApi.requests().registerGetAgeRequestHandler(new ElectrodeBridgeRequestHandler<String, Integer>() {
            @Override
            public void onRequest(@Nullable String payload, @NonNull ElectrodeBridgeResponseListener<Integer> responseListener) {
                assertNotNull(payload);
                assertNotNull(responseListener);
                assertEquals("testName", payload);
                responseListener.onSuccess(20);
                countDownLatch.countDown();
            }
        });

        addMockEventListener(REQUEST_GET_AGE, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                assertNotNull(response);
                assertTrue(response.hasKey(BridgeMessage.BRIDGE_MSG_DATA));
                Integer responseAge = response.getInt(BridgeMessage.BRIDGE_MSG_DATA);
                assertSame(20, responseAge);
                countDownLatch.countDown();
            }

            @Override
            public void onEvent(ReadableMap event) {
                fail();
            }
        });

        WritableMap requestMap = getRequestMap(REQUEST_GET_AGE);
        requestMap.putString(BridgeMessage.BRIDGE_MSG_DATA, "testName");

        ElectrodeReactBridge electrodeReactBridge = ElectrodeBridgeTransceiver.instance();
        electrodeReactBridge.sendMessage(requestMap);

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


    public void testRequestsWithMultipleParamsNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String firstName = "Tom";
        final String lastName = "Jerry";
        final Status status = new Status.Builder(false).build();

        PersonApi.requests().registerUpdatePersonRequestHandler(new ElectrodeBridgeRequestHandler<UpdatePersonRequestData, Person>() {
            @Override
            public void onRequest(@Nullable UpdatePersonRequestData payload, @NonNull ElectrodeBridgeResponseListener<Person> responseListener) {
                assertNotNull(payload);
                responseListener.onSuccess(new Person.Builder((payload.getFirstName() + payload.getLastName()), 0).status(status).build());
            }
        });


        PersonApi.requests().updatePersonPost(new UpdatePersonRequestData.Builder(firstName, lastName, status).build(), new ElectrodeBridgeResponseListener<Person>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Person responseData) {
                assertNotNull(responseData);
                assertEquals((firstName + lastName), responseData.getName());
                assertNotNull(responseData.getStatus());
                assertEquals(status.getMember(), responseData.getStatus().getMember());
                countDownLatch.countDown();
            }
        });


        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testRequestsWithMultipleParamsJSToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String firstName = "Tom";
        final String lastName = "Jerry";
        final Status status = new Status.Builder(false).build();

        ElectrodeBridgeTransceiver transceiver = ElectrodeBridgeTransceiver.instance();

        PersonApi.requests().registerUpdatePersonRequestHandler(new ElectrodeBridgeRequestHandler<UpdatePersonRequestData, Person>() {
            @Override
            public void onRequest(@Nullable UpdatePersonRequestData payload, @NonNull ElectrodeBridgeResponseListener<Person> responseListener) {
                assertNotNull(payload);
                responseListener.onSuccess(new Person.Builder((payload.getFirstName() + payload.getLastName()), 0).status(status).build());
            }
        });


        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_POST_PERSON_UPDATE, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                assertNotNull(response);
                assertTrue(response.hasKey(BridgeMessage.BRIDGE_MSG_DATA));
                ReadableMap person = response.getMap(BridgeMessage.BRIDGE_MSG_DATA);
                assertNotNull(person);
                countDownLatch.countDown();
            }

            @Override
            public void onEvent(ReadableMap event) {
                fail();
            }
        });

        WritableMap requestData = Arguments.createMap();
        requestData.putString("firstName", firstName);
        requestData.putString("lastName", lastName);
        requestData.putMap("status", Arguments.fromBundle(status.toBundle()));

        WritableMap requestMap = getRequestMap(PersonApi.Requests.REQUEST_POST_PERSON_UPDATE);
        requestMap.putMap(BridgeMessage.BRIDGE_MSG_DATA, requestData);

        transceiver.sendMessage(requestMap);


        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testRequestsWithMultipleParamsNativeToJS() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String firstName = "Tom";
        final String lastName = "Jerry";
        final Status status = new Status.Builder(false).build();

        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_POST_PERSON_UPDATE, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(request);
                ReadableMap requestData = request.getMap(BridgeMessage.BRIDGE_MSG_DATA);
                assertNotNull(requestData);
                Status _status = new Status.Builder(requestData.getMap("status").getBoolean("member")).build();
                Person person = new Person.Builder(requestData.getString("firstName") + requestData.getString("lastName"), 0).status(_status).build();
                jsResponseDispatcher.dispatchResponse(Arguments.fromBundle(person.toBundle()));
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


        PersonApi.requests().updatePersonPost(new UpdatePersonRequestData.Builder(firstName, lastName, status).build(), new ElectrodeBridgeResponseListener<Person>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable Person responseData) {
                assertNotNull(responseData);
                assertEquals((firstName + lastName), responseData.getName());
                assertNotNull(responseData.getStatus());
                assertEquals(status.getMember(), responseData.getStatus().getMember());
                countDownLatch.countDown();
            }
        });


        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }


    public void testRequestsWithComplexObjectListNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Status status = new Status.Builder(true).log(false).build();
        final Status status1 = new Status.Builder(true).log(false).build();
        final Status status2 = new Status.Builder(true).log(false).build();
        List<Status> statusList = new ArrayList<Status>() {{
            add(status);
            add(status1);
            add(status2);
        }};


        final Person person = new Person.Builder("test1", 1).build();
        final Person person1 = new Person.Builder("test2", 2).build();
        final Person person2 = new Person.Builder("test3", 3).build();
        final List<Person> personList = new ArrayList<Person>() {{
            add(person);
            add(person1);
            add(person2);
        }};

        PersonApi.requests().registerFindPersonsByStatus(new ElectrodeBridgeRequestHandler<List<Status>, List<Person>>() {
            @Override
            public void onRequest(@Nullable List<Status> payload, @NonNull ElectrodeBridgeResponseListener<List<Person>> responseListener) {
                assertNotNull(payload);
                responseListener.onSuccess(personList);
            }
        });


        PersonApi.requests().findPersonsByStatus(statusList, new ElectrodeBridgeResponseListener<List<Person>>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {

            }

            @Override
            public void onSuccess(@Nullable List<Person> responseData) {
                assertNotNull(responseData);
                for (int i = 0; i < personList.size(); i++) {
                    Person expected = personList.get(i);
                    Person actual = responseData.get(i);
                    assertNotNull(actual);
                    assertEquals(expected.getName(), actual.getName());
                    assertEquals(expected.getMonth(), actual.getMonth());
                }
                countDownLatch.countDown();
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testRequestsWithComplexObjectListNativeToJS() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final Status status = new Status.Builder(true).log(false).build();
        final Status status1 = new Status.Builder(true).log(false).build();
        final Status status2 = new Status.Builder(true).log(false).build();
        List<Status> statusList = new ArrayList<Status>() {{
            add(status);
            add(status1);
            add(status2);
        }};


        final Person person = new Person.Builder("test1", 1).build();
        final Person person1 = new Person.Builder("test2", 2).build();
        final Person person2 = new Person.Builder("test3", 3).build();
        final List<Person> personList = new ArrayList<Person>() {{
            add(person);
            add(person1);
            add(person2);
        }};


        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_FIND_PERSONS_BY_STATUS, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(request);
                jsResponseDispatcher.dispatchResponse(Arguments.fromBundle(BridgeArguments.generateDataBundle(personList)));
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

        PersonApi.requests().findPersonsByStatus(statusList, new ElectrodeBridgeResponseListener<List<Person>>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {

            }

            @Override
            public void onSuccess(@Nullable List<Person> responseData) {
                assertNotNull(responseData);
                for (int i = 0; i < personList.size(); i++) {
                    Person expected = personList.get(i);
                    Person actual = responseData.get(i);
                    assertNotNull(actual);
                    assertEquals(expected.getName(), actual.getName());
                    assertEquals(expected.getMonth(), actual.getMonth());
                }
                countDownLatch.countDown();
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }

    public void testRequestsWithComplexObjectListJSToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final Status status = new Status.Builder(true).log(false).build();
        final Status status1 = new Status.Builder(true).log(false).build();
        final Status status2 = new Status.Builder(true).log(false).build();

        final Person person = new Person.Builder("test1", 1).build();
        final Person person1 = new Person.Builder("test2", 2).build();
        final Person person2 = new Person.Builder("test3", 3).build();
        final List<Person> personList = new ArrayList<Person>() {{
            add(person);
            add(person1);
            add(person2);
        }};

        PersonApi.requests().registerFindPersonsByStatus(new ElectrodeBridgeRequestHandler<List<Status>, List<Person>>() {
            @Override
            public void onRequest(@Nullable List<Status> payload, @NonNull ElectrodeBridgeResponseListener<List<Person>> responseListener) {
                assertNotNull(payload);
                responseListener.onSuccess(personList);
                countDownLatch.countDown();
            }
        });

        UUID uuid = addMockEventListener(PersonApi.Requests.REQUEST_FIND_PERSONS_BY_STATUS, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                assertNotNull(response);
                ReadableArray readableArray = response.getArray(BridgeMessage.BRIDGE_MSG_DATA);
                assertNotNull(readableArray);
                List<Person> actualPersonList = (List<Person>) BridgeArguments.generateObject(ArgumentsEx.toBundle(response, BridgeMessage.BRIDGE_MSG_DATA), Person.class);
                assertNotNull(actualPersonList);
                for (int i = 0; i < personList.size(); i++) {
                    Person expected = personList.get(i);
                    Person actual = actualPersonList.get(i);
                    assertEquals(expected.getName(), actual.getName());
                    assertEquals(expected.getMonth(), actual.getMonth());
                }
                countDownLatch.countDown();
            }

            @Override
            public void onEvent(ReadableMap event) {
                fail();
            }
        });


        WritableArray writableArray = Arguments.createArray();
        writableArray.pushMap(Arguments.fromBundle(status.toBundle()));
        writableArray.pushMap(Arguments.fromBundle(status1.toBundle()));
        writableArray.pushMap(Arguments.fromBundle(status2.toBundle()));

        WritableMap request = getRequestMap(PersonApi.Requests.REQUEST_FIND_PERSONS_BY_STATUS);
        request.putArray(ElectrodeBridgeRequest.BRIDGE_MSG_DATA, writableArray);

        ElectrodeReactBridge reactBridge = ElectrodeBridgeTransceiver.instance();
        reactBridge.sendMessage(request);

        waitForCountDownToFinishOrFail(countDownLatch);
        removeMockEventListener(uuid);
    }


    public void testRequestsWithPrimitiveListNativeToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final List<String> namesList = new ArrayList<String>() {{
            add("name1");
            add("name2");
            add("name3");
        }};

        final List<Integer> ageList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};

        PersonApi.requests().registerFindPersonsAgeByName(new ElectrodeBridgeRequestHandler<List<String>, List<Integer>>() {
            @Override
            public void onRequest(@Nullable List<String> payload, @NonNull ElectrodeBridgeResponseListener<List<Integer>> responseListener) {
                assertNotNull(payload);
                responseListener.onSuccess(ageList);
            }
        });

        PersonApi.requests().findPersonsAgeByName(namesList, new ElectrodeBridgeResponseListener<List<Integer>>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable List<Integer> responseData) {
                assertNotNull(responseData);
                for (int i = 0; i < namesList.size(); i++) {
                    Integer expected = ageList.get(i);
                    Integer actual = responseData.get(i);
                    assertNotNull(actual);
                    assertEquals(expected, actual);
                }
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testRequestsWithPrimitiveListNativeToJS() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final List<String> namesList = new ArrayList<String>() {{
            add("name1");
            add("name2");
            add("name3");
        }};

        final List<Integer> ageList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};

        addMockEventListener(PersonApi.Requests.REQUEST_FIND_PERSONS_AGE_BY_NAME, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                assertNotNull(request);
                jsResponseDispatcher.dispatchResponse(Arguments.fromBundle(BridgeArguments.generateDataBundle(ageList)));
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

        PersonApi.requests().findPersonsAgeByName(namesList, new ElectrodeBridgeResponseListener<List<Integer>>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                fail();
            }

            @Override
            public void onSuccess(@Nullable List<Integer> responseData) {
                assertNotNull(responseData);
                for (int i = 0; i < namesList.size(); i++) {
                    Integer expected = ageList.get(i);
                    Integer actual = responseData.get(i);
                    assertNotNull(actual);
                    assertEquals(expected, actual);
                }
                countDownLatch.countDown();
            }
        });

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    public void testRequestsWithPrimitiveListJSToNative() {
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        final List<Integer> ageList = new ArrayList<Integer>() {{
            add(1);
            add(2);
            add(3);
        }};

        PersonApi.requests().registerFindPersonsAgeByName(new ElectrodeBridgeRequestHandler<List<String>, List<Integer>>() {
            @Override
            public void onRequest(@Nullable List<String> payload, @NonNull ElectrodeBridgeResponseListener<List<Integer>> responseListener) {
                assertNotNull(payload);
                responseListener.onSuccess(ageList);
                countDownLatch.countDown();
            }
        });

        addMockEventListener(PersonApi.Requests.REQUEST_FIND_PERSONS_AGE_BY_NAME, new MockElectrodeEventListener() {
            @Override
            public void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher) {
                fail();
            }

            @Override
            public void onResponse(ReadableMap response) {
                assertNotNull(response);
                countDownLatch.countDown();
            }

            @Override
            public void onEvent(ReadableMap event) {
                fail();
            }
        });

        WritableArray writableArray = Arguments.createArray();
        writableArray.pushString("name1");
        writableArray.pushString("name2");
        writableArray.pushString("name3");

        WritableMap request = getRequestMap(PersonApi.Requests.REQUEST_FIND_PERSONS_AGE_BY_NAME);
        request.putArray(ElectrodeBridgeRequest.BRIDGE_MSG_DATA, writableArray);

        ElectrodeReactBridge reactBridge = ElectrodeBridgeTransceiver.instance();
        reactBridge.sendMessage(request);


        waitForCountDownToFinishOrFail(countDownLatch);
    }
}