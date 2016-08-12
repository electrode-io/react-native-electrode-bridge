package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultEventRegistrar<T> implements EventRegistrar<T> {
    private final ConcurrentHashMap<UUID, T> mEventListenerByUUID = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<T>> mEventListenersByEventType = new ConcurrentHashMap<>();

    /**
     * Registers an event listener
     *
     * @param type The type of event this listener is interested in
     * @param eventListener The event listener
     * @return A UUID to pass back to unregisterEventListener
     */
    @SuppressWarnings("unused")
    public UUID registerEventListener(@NonNull String type, @NonNull T eventListener) {
        UUID eventListenerUuid = UUID.randomUUID();
        if (mEventListenersByEventType.containsKey(type)) {
            mEventListenersByEventType.get(type).add(eventListener);
        } else {
            List<T> eventListeners = new ArrayList<>();
            eventListeners.add(eventListener);
            mEventListenersByEventType.put(type, eventListeners);
        }
        mEventListenerByUUID.put(eventListenerUuid, eventListener);
        return eventListenerUuid;
    }

    /**
     * Unregisters an event listener
     *
     * @param eventListenerUuid The UUID that was obtained through initial registerEventListener
     * call
     */
    @SuppressWarnings("unused")
    public void unregisterEventListener(@NonNull UUID eventListenerUuid) {
        T eventListener = mEventListenerByUUID.remove(eventListenerUuid);
        if (eventListener != null) {
            for (List<T> eventListeners : mEventListenersByEventType.values()) {
                if (eventListeners.contains(eventListener)) {
                    eventListeners.remove(eventListener);
                    break;
                }
            }
        }
    }

    /**
     * Gets the list of all event listeners registered for a given event type
     *
     * @param type The type of the event
     * @return A list of event listeners registered for the given event type or an empty list if no
     * event listeners are currently registered for this event type
     */
    @NonNull
    @Override
    public List<T> getEventListeners(String type) {
        if (!mEventListenersByEventType.containsKey(type)) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(mEventListenersByEventType.get(type));
    }
}
