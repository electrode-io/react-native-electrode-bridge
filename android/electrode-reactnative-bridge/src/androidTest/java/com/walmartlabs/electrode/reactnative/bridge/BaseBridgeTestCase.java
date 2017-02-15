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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by d0g00g4 on 2/14/17.
 */

public class BaseBridgeTestCase extends InstrumentationTestCase {

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
            this.electrodeBridgeInternal = ElectrodeBridgeInternal.create(new ReactContextWrapper() {
                @Override
                public void emitEvent(@NonNull String eventName, @Nullable WritableMap message) {
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
            });
            modules.add(electrodeBridgeInternal);
            return modules;
        }
    }

    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    void waitForCountDownToFinishOrFail(CountDownLatch countDown) {
        try {
            assertTrue(countDown.await(1000, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail();
        }
    }

}
