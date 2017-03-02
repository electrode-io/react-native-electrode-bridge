package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReadableMap;


public interface EventDispatcher {
    /**
     * Dispatches an event
     *
     * @param id   The event id
     * @param name The name of the event to dispatch
     * @param data The data of the event as a ReadableMap
     */
    void dispatchEvent(@NonNull String id, @NonNull String name, @NonNull ReadableMap data);
}
