package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

/**
 * Class that takes care of registering an event listener to the bridge.
 *
 * @param <T> Event payload type: Accepted types are Primitive wrappers and {@link Bridgeable}
 */

public class EventListenerProcessor<T> extends BridgeProcessor {
    private static final String TAG = EventListenerProcessor.class.getSimpleName();

    private final String eventName;
    private final Class<T> eventPayLoadClass;
    private final ElectrodeBridgeEventListener<T> eventListener;

    public EventListenerProcessor(@NonNull String eventName, @NonNull final Class<T> eventPayLoadClass, @NonNull final ElectrodeBridgeEventListener<T> eventListener) {
        this.eventName = eventName;
        this.eventPayLoadClass = eventPayLoadClass;
        this.eventListener = eventListener;
    }

    @Override
    public void execute() {
        ElectrodeBridgeEventListener<ElectrodeBridgeEvent> intermediateEventListener = new ElectrodeBridgeEventListener<ElectrodeBridgeEvent>() {
            @Override
            public void onEvent(@Nullable ElectrodeBridgeEvent bridgeEvent) {
                if (bridgeEvent == null) {
                    throw new IllegalArgumentException("bridgeEvent cannot be null, should never reach here");
                }

                Logger.d(TAG, "Processing final result for the event with payload bundle(%s)", bridgeEvent);

                T result = null;
                if (eventPayLoadClass != None.class) {
                    result = (T) BridgeArguments.generateObject(bridgeEvent.getData(), eventPayLoadClass);
                    result = (T) preProcessObject(result, eventPayLoadClass);
                }

                eventListener.onEvent(result);
            }
        };
        ElectrodeBridgeHolder.addEventListener(eventName, intermediateEventListener);
    }
}
