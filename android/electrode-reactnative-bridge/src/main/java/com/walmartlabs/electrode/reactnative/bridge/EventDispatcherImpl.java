package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

@SuppressWarnings("unused")
public class EventDispatcherImpl implements ElectrodeBridgeInternal.EventDispatcher {

    private static final String TAG = EventDispatcherImpl.class.getSimpleName();

    private final EventRegistrar<ElectrodeBridgeEventListener> mEventRegistrar;
    private static final Bundle EMPTY_BUNDLE = new Bundle();

    public EventDispatcherImpl(EventRegistrar<ElectrodeBridgeEventListener> eventRegistrar) {
        mEventRegistrar = eventRegistrar;
    }

    /**
     * Dispatch an event
     *
     * @param id      The event id
     * @param name    The name of the event to dispatch
     * @param payload The payload of the event as a ReadableMap
     */
    @Override
    public void dispatchEvent(@NonNull String id, @NonNull String name, @NonNull ReadableMap payload) {
        for (ElectrodeBridgeEventListener eventListener : mEventRegistrar.getEventListeners(name)) {
            Bundle bundle = ArgumentsEx.toBundle(payload);
            Logger.d(TAG, "Event dispatcher is dispatching event(%s), id(%s) to listener(%s)", name, id, eventListener);
            eventListener.onEvent(bundle != null ? bundle : EMPTY_BUNDLE);
        }
    }
}
