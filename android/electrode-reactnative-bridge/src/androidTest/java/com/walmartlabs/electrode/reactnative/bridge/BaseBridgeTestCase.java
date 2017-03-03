package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.test.InstrumentationTestCase;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.shell.MainReactPackage;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeInternal.BRIDGE_MSG_ID;
import static com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeInternal.BRIDGE_REQUEST_ID;

public class BaseBridgeTestCase extends InstrumentationTestCase {

    private static final int REQUEST_TIMEOUT_SECONDS = 10;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Logger.overrideLogLevel(Logger.LogLevel.DEBUG);
        initBridge();
    }

    private void initBridge() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ReactInstanceManager reactInstanceManager = null;
        final ElectrodeBridgePackage mockElectrodePackage = new MockElectrodePackage();
        try {
            reactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(this.getInstrumentation().newApplication(MyTestApplication.class.getClassLoader(), MyTestApplication.class.getName(), getInstrumentation().getContext()))
                    .setBundleAssetName("index.android.bundle")
                    .setJSMainModuleName("index.android")
                    .addPackage(new MainReactPackage())
                    .setUseDeveloperSupport(false)
                    .setInitialLifecycleState(LifecycleState.BEFORE_CREATE)
                    .addPackage(mockElectrodePackage)
                    .build();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if (reactInstanceManager != null) {
            final ReactInstanceManager finalReactInstanceManager = reactInstanceManager;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalReactInstanceManager.createReactContextInBackground();
                }
            });

            reactInstanceManager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                @Override
                public void onReactContextInitialized(ReactContext context) {
                    mockElectrodePackage.onReactNativeInitialized();
                    countDownLatch.countDown();
                }
            });
        }

        waitForCountDownToFinishOrFail(countDownLatch);
    }

    private class MockElectrodePackage extends ElectrodeBridgePackage {

        @Override
        public List<NativeModule> createNativeModules(final ReactApplicationContext reactContext) {
            List<NativeModule> modules = new ArrayList<>();
            this.electrodeBridgeInternal = ElectrodeBridgeInternal.create(getReactContextWrapper(reactContext));
            modules.add(electrodeBridgeInternal);
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
            public void emitEvent(@NonNull String eventName, @Nullable WritableMap message) {
                assertNotNull(message);
                assertNotNull(message.getString("name"));
                handleContextWrapperEmitEvent(message.getString("name"), message);
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
     * This is mainly exposed to mock a JS side event handling. This is called when {@link ElectrodeBridge} emits en event to JS side to handle a request or event.
     *
     * @param eventName {@link String}
     * @param message   {@link WritableMap}
     */
    private void handleContextWrapperEmitEvent(@NonNull String eventName, @Nullable final WritableMap message) {
        for (MockElectrodeEventListener listener : mockEventRegistrar.getEventListeners(eventName)) {
            listener.onEvent(eventName, message, new MockJsResponseDispatcher() {
                @Override
                public void dispatchResponse(@NonNull WritableMap response) {
                    assertNotNull(message);
                    assertNotNull(message.getString(BRIDGE_MSG_ID));
                    response.putString(BRIDGE_REQUEST_ID, message.getString(BRIDGE_MSG_ID));
                    ElectrodeBridgeInternal.instance().dispatchEvent(ElectrodeBridgeInternal.BRIDGE_RESPONSE, null, response);
                }
            });
        }
    }

    private static final EventRegistrar<MockElectrodeEventListener> mockEventRegistrar = new EventRegistrarImpl<>();

    UUID addMockEventListener(@NonNull String eventName, @NonNull MockElectrodeEventListener mockElectrodeEventListener) {
        UUID uuid = mockEventRegistrar.registerEventListener(eventName, mockElectrodeEventListener);
        assertNotNull(uuid);
        assertTrue(mockEventRegistrar.getEventListeners(eventName).size() > 0);
        return uuid;
    }

    void removeMockEventListener(UUID uuid){
        mockEventRegistrar.unregisterEventListener(uuid);
    }

    /**
     * This interface is a mock representation of JS side receiving an event. A call to {@link MockElectrodeEventListener#onEvent(String, WritableMap, MockJsResponseDispatcher)} ensures that that the given request will be delivered to JS side.
     */
    interface MockElectrodeEventListener {
        void onEvent(@NonNull String eventName, @Nullable WritableMap message, @NonNull MockJsResponseDispatcher jsResponseDispatcher);
    }

    interface MockJsResponseDispatcher {
        void dispatchResponse(@NonNull final WritableMap response);
    }

}
