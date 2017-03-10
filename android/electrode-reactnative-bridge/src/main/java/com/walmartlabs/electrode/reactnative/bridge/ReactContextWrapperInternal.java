package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public class ReactContextWrapperInternal implements ReactContextWrapper {
    private static final String TAG = ReactContextWrapperInternal.class.getSimpleName();

    private final ReactApplicationContext mReactApplicationContext;

    public ReactContextWrapperInternal(@NonNull ReactApplicationContext reactApplicationContext) {
        mReactApplicationContext = reactApplicationContext;
    }

    @Override
    public void emitEvent(@NonNull BridgeMessage event) {
        Logger.d(TAG, "emitting event(id=%s, name=%s, type=%s) to JS", event.getId(), event.getName(), event.getType());
        mReactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("electrode.bridge.message", event.map());
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
