package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.UUID;

/**
 * Processes an event that is emitted from Native side
 *
 * @param <T> eventPayload
 */

public class EventProcessor<T> implements Processor {
    private static final String TAG = EventProcessor.class.getSimpleName();

    private final T eventPayload;
    private final String eventName;

    public EventProcessor(@NonNull String eventName, @Nullable T eventPayload) {
        this.eventPayload = eventPayload;
        this.eventName = eventName;
    }

    @Override
    public UUID execute() {
        Logger.d(TAG, "EventProcessor is emitting event(%s), with payload(%s)", eventName, eventPayload);
        ElectrodeBridgeHolder.emitEvent(new ElectrodeBridgeEvent.Builder(eventName)
                .withData(eventPayload)
                .build());
        return null;
    }
}
