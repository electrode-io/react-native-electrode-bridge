package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public class EventDispatcherImpl implements EventDispatcher {

    private static final String TAG = EventDispatcherImpl.class.getSimpleName();

    private final EventRegistrar<ElectrodeBridgeEventListener<ElectrodeBridgeEvent>> mEventRegistrar;

    public EventDispatcherImpl(EventRegistrar<ElectrodeBridgeEventListener<ElectrodeBridgeEvent>> eventRegistrar) {
        mEventRegistrar = eventRegistrar;
    }

    @Override
    public void dispatchEvent(@NonNull ElectrodeBridgeEvent bridgeEvent) {
        for (ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener : mEventRegistrar.getEventListeners(bridgeEvent.getName())) {
            Logger.d(TAG, "Event dispatcher is dispatching event(%s), id(%s) to listener(%s)", bridgeEvent.getName(), bridgeEvent.getId(), eventListener);
            eventListener.onEvent(bridgeEvent);
        }
    }
}
