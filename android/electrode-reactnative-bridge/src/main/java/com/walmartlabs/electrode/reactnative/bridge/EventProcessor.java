package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

/**
 * Processes an event that is emitted from Native side
 *
 * @param <T> eventPayload
 */

public class EventProcessor<T> {

    private final T eventPayload;
    private final String eventName;

    public EventProcessor(@NonNull String eventName, @Nullable T eventPayload) {
        this.eventPayload = eventPayload;
        this.eventName = eventName;
    }

    public void execute() {
        Bundle data = BridgeArguments.generateRequestBundle(eventPayload);
        ElectrodeBridgeHolder.emitEvent(new ElectrodeBridgeEvent.Builder(eventName)
                .withData(data)
                .build());
    }
}
