package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

/**
 * A wrapper that is used inside the bridge to communicate to react native modules.
 * <p>
 * This wrapper helps to provide multiple implementations to the bridge for mock support
 */

interface ReactContextWrapper {
    void emitEvent(@NonNull BridgeMessage event);

    void runOnUiQueueThread(@NonNull Runnable runnable);

    @NonNull
    ReactApplicationContext getContext();
}
