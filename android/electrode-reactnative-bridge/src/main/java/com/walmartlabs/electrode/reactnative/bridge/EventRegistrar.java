package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.UUID;

public interface EventRegistrar<T> {

    /**
     * Registers an event listener
     *
     * @param name The event name this listener is interested in
     * @param eventListener The event listener
     * @return A UUID to pass back to unregisterEventListener
     */
    @NonNull
    UUID registerEventListener(@NonNull String name, @NonNull T eventListener);

    /**
     * Unregisters an event listener
     *
     * @param eventListenerUuid The UUID that was obtained through initial registerEventListener
     * call
     */
    @SuppressWarnings("unused")
    void unregisterEventListener(@NonNull UUID eventListenerUuid);

    /**
     * Gets the list of all event listeners registered for a given event name
     *
     * @param name The name of the event
     * @return A list of event listeners registered for the given event name or an empty list if no
     * event listeners are currently registered for this event name
     */
    @NonNull
    List<T> getEventListeners(@NonNull String name);

}
