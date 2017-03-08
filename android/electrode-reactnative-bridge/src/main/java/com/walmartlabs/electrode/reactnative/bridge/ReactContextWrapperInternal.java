package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class ReactContextWrapperInternal implements ReactContextWrapper {

    private final ReactApplicationContext mReactApplicationContext;

    public ReactContextWrapperInternal(@NonNull ReactApplicationContext reactApplicationContext) {
        mReactApplicationContext = reactApplicationContext;
    }

    @Override
    public void emitEvent(@NonNull BridgeMessage event) {
        mReactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(event.getName(), event.map());
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
