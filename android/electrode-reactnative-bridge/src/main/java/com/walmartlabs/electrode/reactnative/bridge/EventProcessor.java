package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

/**
 * Processes an event that is emitted from Native side
 *
 * @param <T> eventPayload
 */

public class EventProcessor<T> {
    private static final String TAG = EventProcessor.class.getSimpleName();

    private final T eventPayload;
    private final String eventName;

    public EventProcessor(@NonNull String eventName, @Nullable T eventPayload) {
        this.eventPayload = eventPayload;
        this.eventName = eventName;
    }

    public void execute() {
        Logger.d(TAG, "EventProcessor is emitting event(%s), with payload(%s)", eventName, eventPayload);
        Bundle data = BridgeArguments.generateBundle(eventPayload, BridgeArguments.Type.EVENT);
        ElectrodeBridgeHolder.emitEvent(new ElectrodeBridgeEvent.Builder(eventName)
                .withDispatchMode(ElectrodeBridgeEvent.DispatchMode.NATIVE)
                .withData(data)
                .build());
    }
}
