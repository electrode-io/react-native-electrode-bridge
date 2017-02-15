package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by d0g00g4 on 2/14/17.
 */

public class ReactContextWrapperInternal implements ReactContextWrapper {

    private final ReactApplicationContext mReactApplicationContext;

    public ReactContextWrapperInternal(@NonNull ReactApplicationContext reactApplicationContext) {
        mReactApplicationContext = reactApplicationContext;
    }

    @Override
    public void emitEvent(@NonNull String eventName, @Nullable WritableMap message) {
        mReactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, message);
    }

    @Override
    public void runOnUiQueueThread(@NonNull Runnable runnable) {
        mReactApplicationContext.runOnUiQueueThread(runnable);
    }

    @NonNull
    @Override
    public ReactApplicationContext getContext() {
        return mReactApplicationContext;
    }
}
