/*
 * Copyright 2017 WalmartLabs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.walmartlabs.electrode.reactnative.bridge;

import android.app.Instrumentation;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class BaseBridgeTestCase {

    private static final int REQUEST_TIMEOUT_SECONDS = 30;

    @Before
    public void setUp() throws Exception {
        Logger.overrideLogLevel(Logger.LogLevel.DEBUG);
        initBridge();
        ElectrodeBridgeTransceiver.instance().debug_ClearRequestHandlerRegistrar();
        ((EventRegistrarImpl) mockEventRegistrar).reset();
    }

    private Instrumentation getInstrumentation() {
        return InstrumentationRegistry.getInstrumentation();
    }

    private void initBridge() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ElectrodeBridgePackage mockElectrodePackage = new MockElectrodePackage();
        final Instrumentation instrumentation = getInstrumentation();
        // Fixes : com.facebook.react.bridge.AssertionException: Expected to run on UI thread!
        // react-native version : 0.45
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ReactInstanceManager reactInstanceManager;
                try {
                    reactInstanceManager = ReactInstanceManager.builder()
                            .setApplication(instrumentation.newApplication(MyTestApplication.class.getClassLoader(), MyTestApplication.class.getName(), getInstrumentation().getContext()))
                            .setBundleAssetName("index.android.bundle")
                            .addPackage(new MainReactPackage())
                            .setUseDeveloperSupport(false)
                            .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
                            .addPackage(mockElectrodePackage)
                            .build();
                    reactInstanceManager.createReactContextInBackground();
                    reactInstanceManager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                        @Override
                        public void onReactContextInitialized(ReactContext context) {
                            mockElectrodePackage.onReactNativeInitialized();
                            countDownLatch.countDown();
                        }
                    });
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        waitForCountDownToFinishOrFail(countDownLatch);
    }

    private class MockElectrodePackage extends ElectrodeBridgePackage {

        @Override
        public List<NativeModule> createNativeModules(final ReactApplicationContext reactContext) {
            List<NativeModule> modules = new ArrayList<>();
            this.electrodeBridgeTransceiver = ElectrodeBridgeTransceiver.create(getReactContextWrapper(reactContext));
            modules.add(electrodeBridgeTransceiver);
            return modules;
        }
    }

    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    void waitForCountDownToFinishOrFail(CountDownLatch countDown) {
        try {
            assertTrue(countDown.await(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail();
        }
    }

    /**
     * Returns a default ReactContextWrapper, overide if needed.
     *
     * @param reactContext {@link ReactApplicationContext}
     * @return ReactContextWrapper
     */
    @NonNull
    private ReactContextWrapper getReactContextWrapper(final ReactApplicationContext reactContext) {
        return new ReactContextWrapper() {
            @Override
            public void emitEvent(@NonNull BridgeMessage event) {
                assertNotNull(event);
                assertNotNull(event.getName());
                mockJsEventHandler(event.map());
            }

            @Override
            public void runOnUiQueueThread(@NonNull Runnable runnable) {
                runOnUiThread(runnable);
            }

            @NonNull
            @Override
            public ReactApplicationContext getContext() {
                return reactContext;
            }
        };
    }

    /**
     * This is mainly exposed to mock a JS side event handling. This is called when {@link ElectrodeNativeBridge} emits en event to JS side to handle a request or event.
     *
     * @param inputMessage {@link WritableMap}
     */
    private void mockJsEventHandler(@Nullable final ReadableMap inputMessage) {
        assertNotNull(inputMessage);
        BridgeMessage.Type type = BridgeMessage.Type.getType(inputMessage.getString(BridgeMessage.BRIDGE_MSG_TYPE));
        assertNotNull(type);
        String eventName = inputMessage.getString(BridgeMessage.BRIDGE_MSG_NAME);
        assertNotNull(eventName);

        for (MockElectrodeEventListener listener : mockEventRegistrar.getEventListeners(eventName)) {

            switch (type) {
                case EVENT:
                    listener.onEvent(inputMessage);
                    break;
                case REQUEST:
                    listener.onRequest(inputMessage, new MockJsResponseDispatcher() {
                        @Override
                        public void dispatchResponse(@Nullable WritableMap responseData) {
                            WritableMap finalResponse = Arguments.createMap();
                            finalResponse.putString(ElectrodeBridgeResponse.BRIDGE_MSG_ID, inputMessage.getString(ElectrodeBridgeRequest.BRIDGE_MSG_ID));
                            finalResponse.putString(ElectrodeBridgeResponse.BRIDGE_MSG_NAME, inputMessage.getString(ElectrodeBridgeRequest.BRIDGE_MSG_NAME));
                            finalResponse.putString(ElectrodeBridgeResponse.BRIDGE_MSG_TYPE, BridgeMessage.Type.RESPONSE.getKey());
                            if (responseData != null) {
                                if (responseData.hasKey(ElectrodeBridgeResponse.BRIDGE_MSG_DATA)) {
                                    //This is used for response coming wth primitives instead of complex objects.
                                    finalResponse.merge(responseData);
                                } else {
                                    finalResponse.putMap(ElectrodeBridgeResponse.BRIDGE_MSG_DATA, responseData);
                                }
                            }
                            ElectrodeBridgeTransceiver.instance().sendMessage(finalResponse);
                        }
                    });
                    break;
                case RESPONSE:
                    listener.onResponse(inputMessage);
                    break;
            }
        }
    }

    private static final EventRegistrar<MockElectrodeEventListener> mockEventRegistrar = new EventRegistrarImpl<>();

    UUID addMockEventListener(@NonNull String eventName, @NonNull MockElectrodeEventListener mockElectrodeEventListener) {
        final UUID uuid = UUID.randomUUID();
        boolean isRegistered = mockEventRegistrar.registerEventListener(eventName, mockElectrodeEventListener, uuid);
        assertTrue(isRegistered);
        assertTrue(mockEventRegistrar.getEventListeners(eventName).size() > 0);
        return uuid;
    }

    void removeMockEventListener(UUID uuid) {
        mockEventRegistrar.unregisterEventListener(uuid);
    }

    /**
     * Creates a MAP representation of a event coming from JS side with the given name and data.
     *
     * @param TEST_EVENT_NAME {@link String}
     * @return WritableMap
     */
    WritableMap createTestEventMap(String TEST_EVENT_NAME) {
        WritableMap eventMap = Arguments.createMap();
        eventMap.putString(ElectrodeBridgeEvent.BRIDGE_MSG_ID, ElectrodeBridgeEvent.getUUID());
        eventMap.putString(ElectrodeBridgeEvent.BRIDGE_MSG_NAME, TEST_EVENT_NAME);
        eventMap.putString(ElectrodeBridgeEvent.BRIDGE_MSG_TYPE, BridgeMessage.Type.EVENT.getKey());
        return eventMap;
    }

    WritableMap getRequestMap(String requestName) {
        WritableMap request = Arguments.createMap();
        request.putString(ElectrodeBridgeRequest.BRIDGE_MSG_ID, ElectrodeBridgeRequest.getUUID());
        request.putString(ElectrodeBridgeRequest.BRIDGE_MSG_NAME, requestName);
        request.putString(ElectrodeBridgeRequest.BRIDGE_MSG_TYPE, BridgeMessage.Type.REQUEST.getKey());
        return request;
    }

    ElectrodeReactBridge getReactBridge() {
        return ElectrodeBridgeTransceiver.instance();
    }

    ElectrodeNativeBridge getNativeBridge() {
        return ElectrodeBridgeTransceiver.instance();
    }


    /**
     * This interface is a mock representation of JS side receiving an event.
     */
    interface MockElectrodeEventListener {
        /**
         * Mocks JS side receiving a request
         */
        void onRequest(ReadableMap request, @NonNull MockJsResponseDispatcher jsResponseDispatcher);

        /**
         * Mocks JS side receiving a response
         */
        void onResponse(ReadableMap response);

        /**
         * Mocks JS side receiving an event
         */
        void onEvent(ReadableMap event);
    }

    interface MockJsResponseDispatcher {
        void dispatchResponse(@Nullable final WritableMap response);
    }


    class TestMockEventListener implements MockElectrodeEventListener {

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
            fail();
        }
    }
}
