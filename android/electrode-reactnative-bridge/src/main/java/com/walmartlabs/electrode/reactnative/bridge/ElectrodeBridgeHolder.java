package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client facing class.
 * Facade to ElectrodeBridgeTransceiver.
 * Handles queuing every method calls until react native is ready.
 */
public final class ElectrodeBridgeHolder {

    private static final String TAG = ElectrodeBridgeHolder.class.getSimpleName();

    private static boolean isReactNativeReady;

    private static ElectrodeBridge electrodeBridge;

    // We queue requests/events as long as react native initialization is not complete.
    // Indeed, if a client of the bridge calls `sendRequest` upon it's application start,
    // it will throw an exception due to the fact that react native initialization is not
    // complete (react native bridge not ready). RN initialization is asynchronous.
    // Doing this greatly simplifies things for the electrode bridge client as he does not
    // have to bother with burdensome code to wait for RN to be ready. We take care of that !
    // This solution does not really scale in the sense that if the user sends a 1000 requests
    // upon native app start, it can become problematic. But I don't see why a user would do that
    // unless it's a bug in its app
    private static final HashMap<String, ElectrodeBridgeRequestHandler<Bundle, Object>> mQueuedRequestHandlersRegistration = new HashMap<>();
    private static final HashMap<String, ElectrodeBridgeEventListener<Bundle>> mQueuedEventListenersRegistration = new HashMap<>();
    private static final HashMap<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener<Bundle>> mQueuedRequests = new HashMap<>();
    private static final List<ElectrodeBridgeEvent> mQueuedEvents = new ArrayList<>();

    static {
        ElectrodeBridgeTransceiver.registerReactNativeReadyListener(new ElectrodeBridgeTransceiver.ReactNativeReadyListener() {
            @Override
            public void onReactNativeReady() {
                isReactNativeReady = true;
                electrodeBridge = ElectrodeBridgeTransceiver.instance();
                registerQueuedEventListeners();
                registerQueuedRequestHandlers();
                sendQueuedRequests();
                emitQueuedEvents();
            }
        });

    }

    /**
     * Emits an event with some data to the JS react native side
     *
     * @param event The event to emit
     */
    @SuppressWarnings("unused")
    public static void emitEvent(@NonNull ElectrodeBridgeEvent event) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing event. Will emit once react native initialization is complete.");
            mQueuedEvents.add(event);
            return;
        }

        electrodeBridge.sendEvent(event);
    }

    /**
     * Sends a request
     *
     * @param request          The request to send
     * @param responseListener Listener to be called upon request completion
     */
    @SuppressWarnings("unused")
    public static void sendRequest(
            @NonNull ElectrodeBridgeRequest request,
            @NonNull final ElectrodeBridgeResponseListener<Bundle> responseListener) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing request(%s). Will send once react native initialization is complete.", request);
            mQueuedRequests.put(request, responseListener);
            return;
        }

        electrodeBridge.sendRequest(request, responseListener);
    }

    /**
     * Registers a request handler
     *
     * @param name           The request name this handler can handle
     * @param requestHandler The request handler instance
     */
    @SuppressWarnings("unused")
    @NonNull
    public static void registerRequestHandler(@NonNull String name,
                                              @NonNull ElectrodeBridgeRequestHandler<Bundle, Object> requestHandler) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing request handler registration for request(name=%s). Will register once react native initialization is complete.", name);
            mQueuedRequestHandlersRegistration.put(name, requestHandler);
            return;
        }

        electrodeBridge.registerRequestHandler(name, requestHandler);
    }

    /**
     * Registers an event listener
     *
     * @param name          The event name this listener is interested in
     * @param eventListener The event listener
     * @return A UUID to pass back to unregisterEventListener
     */
    @SuppressWarnings("unused")
    public static void addEventListener(@NonNull String name,
                                        @NonNull ElectrodeBridgeEventListener<Bundle> eventListener) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing event handler registration for event(name=%s). Will register once react native initialization is complete.", name);
            mQueuedEventListenersRegistration.put(name, eventListener);
            return;
        }

        electrodeBridge.addEventListener(name, eventListener);
    }

    private static void registerQueuedRequestHandlers() {
        for (Map.Entry<String, ElectrodeBridgeRequestHandler<Bundle, Object>> entry : mQueuedRequestHandlersRegistration.entrySet()) {
            electrodeBridge.registerRequestHandler(entry.getKey(), entry.getValue());
        }
        mQueuedRequestHandlersRegistration.clear();
    }

    private static void registerQueuedEventListeners() {
        for (Map.Entry<String, ElectrodeBridgeEventListener<Bundle>> entry : mQueuedEventListenersRegistration.entrySet()) {
            electrodeBridge.addEventListener(entry.getKey(), entry.getValue());
        }
        mQueuedEventListenersRegistration.clear();
    }

    private static void sendQueuedRequests() {
        for (Map.Entry<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener<Bundle>> entry : mQueuedRequests.entrySet()) {
            electrodeBridge.sendRequest(entry.getKey(), entry.getValue());
        }
        mQueuedRequests.clear();
    }

    private static void emitQueuedEvents() {
        for (ElectrodeBridgeEvent event : mQueuedEvents) {
            electrodeBridge.sendEvent(event);
        }
        mQueuedEvents.clear();
    }

}
