package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventRegistrarImpl<T> implements EventRegistrar<T> {
    private final ConcurrentHashMap<UUID, T> mEventListenerByUUID = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<T>> mEventListenersByEventName = new ConcurrentHashMap<>();

    /**
     * Registers an event listener
     *
     * @param name          The event name this listener is interested in
     * @param eventListener The event listener
     * @return A UUID to pass back to unregisterEventListener
     */
    @NonNull
    public UUID registerEventListener(@NonNull String name, @NonNull T eventListener) {
        UUID eventListenerUuid = UUID.randomUUID();
        if (mEventListenersByEventName.containsKey(name)) {
            mEventListenersByEventName.get(name).add(eventListener);
        } else {
            List<T> eventListeners = new ArrayList<>();
            eventListeners.add(eventListener);
            mEventListenersByEventName.put(name, eventListeners);
        }
        mEventListenerByUUID.put(eventListenerUuid, eventListener);
        return eventListenerUuid;
    }

    /**
     * Unregisters an event listener
     *
     * @param eventListenerUuid The UUID that was obtained through initial registerEventListener call
     */
    public void unregisterEventListener(@NonNull UUID eventListenerUuid) {
        T eventListener = mEventListenerByUUID.remove(eventListenerUuid);
        if (eventListener != null) {
            for (List<T> eventListeners : mEventListenersByEventName.values()) {
                if (eventListeners.contains(eventListener)) {
                    eventListeners.remove(eventListener);
                    break;
                }
            }
        }
    }

    /**
     * Gets the list of all event listeners registered for a given event name
     *
     * @param name The event name
     * @return A list of event listeners registered for the given event name or an empty list if no
     * event listeners are currently registered for this event name
     */
    @NonNull
    @Override
    public List<T> getEventListeners(@NonNull String name) {
        if (!mEventListenersByEventName.containsKey(name)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(mEventListenersByEventName.get(name));
    }
}
