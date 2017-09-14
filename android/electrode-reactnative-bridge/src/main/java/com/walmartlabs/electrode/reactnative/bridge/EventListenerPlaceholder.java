package com.walmartlabs.electrode.reactnative.bridge;

import java.util.UUID;

/**
 * Place holder to set event information during deferred registration
 */

public class EventListenerPlaceholder {
    private UUID mUUID;
    private ElectrodeBridgeEventListener<ElectrodeBridgeEvent> mEventListener;

    public EventListenerPlaceholder(UUID uuid, ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener) {
        this.mUUID = uuid;
        this.mEventListener = eventListener;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public ElectrodeBridgeEventListener<ElectrodeBridgeEvent> getEventListener() {
        return mEventListener;
    }
}
