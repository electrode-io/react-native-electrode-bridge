package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provide method to be notified of incoming event
 */
public interface ElectrodeBridgeEventListener<T> {
    /**
     * Called whenever an event matching this event listener is received
     *
     * @param eventPayload The event payload
     */
    void onEvent(@Nullable T eventPayload);
}
