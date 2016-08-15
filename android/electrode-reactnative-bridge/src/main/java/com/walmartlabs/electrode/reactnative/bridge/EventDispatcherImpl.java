package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;

@SuppressWarnings("unused")
public class EventDispatcherImpl implements ElectrodeBridge.EventDispatcher {

    private final EventRegistrar<EventListener> mEventRegistrar;
    private static final Bundle EMPTY_BUNDLE = new Bundle();

    public EventDispatcherImpl(EventRegistrar<EventListener> eventRegistrar) {
        mEventRegistrar = eventRegistrar;
    }

    /**
     * Provide method to be notified of incoming event
     */
    public interface EventListener {
        /**
         * Called whenever an event matching this event listener is received
         * @param payload The event payload
         */
        void onEvent(@NonNull Bundle payload);
    }

    /**
     * Dispatch an event
     * @param id The event id
     * @param type The type of the event to dispatch
     * @param payload The payload of the event as a ReadableMap
     */
    @Override
    public void dispatchEvent(@NonNull String id, @NonNull String type, @NonNull ReadableMap payload) {
        for (EventListener eventListener : mEventRegistrar.getEventListeners(type)) {
            Bundle bundle = Arguments.toBundle(payload);

            eventListener.onEvent(bundle != null ? bundle : EMPTY_BUNDLE);
        }
    }
}
