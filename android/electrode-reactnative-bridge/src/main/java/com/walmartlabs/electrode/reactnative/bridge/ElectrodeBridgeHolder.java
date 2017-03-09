package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client facing class.
 * Facade to ElectrodeBridgeInternal.
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
    private static final HashMap<String, ElectrodeBridgeRequestHandler> mQueuedRequestHandlersRegistration = new HashMap<>();
    private static final HashMap<String, ElectrodeBridgeEventListener> mQueuedEventListenersRegistration = new HashMap<>();
    private static final HashMap<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener> mQueuedRequests = new HashMap<>();
    private static final List<ElectrodeBridgeEvent> mQueuedEvents = new ArrayList<>();

    static {
        ElectrodeBridgeInternal.registerReactNativeReadyListener(new ElectrodeBridgeInternal.ReactNativeReadyListener() {
            @Override
            public void onReactNativeReady() {
                isReactNativeReady = true;
                electrodeBridge = ElectrodeBridgeInternal.instance();
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
            Log.d(TAG, "Queuing event. Will emit once react native initialization is complete.");
            mQueuedEvents.add(event);
            return;
        }

        electrodeBridge.emitEvent(event);
    }

    /**
     * Sends a request
     *
     * @param request            The request to send
     * @param responseListener Listener to be called upon request completion
     */
    @SuppressWarnings("unused")
    public static void sendRequest(
            @NonNull ElectrodeBridgeRequest request,
            @NonNull final ElectrodeBridgeResponseListener<Bundle> responseListener) {
        if (!isReactNativeReady) {
            Log.d(TAG, "Queuing request. Will send once react native initialization is complete.");
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
            Log.d(TAG, "Queuing request handler registration. Will register once react native initialization is complete.");
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
            Log.d(TAG, "Queuing event handler registration. Will register once react native initialization is complete.");
            mQueuedEventListenersRegistration.put(name, eventListener);
            return;
        }

        electrodeBridge.addEventListener(name, eventListener);
    }

    private static void registerQueuedRequestHandlers() {
        for (Map.Entry<String, ElectrodeBridgeRequestHandler> entry : mQueuedRequestHandlersRegistration.entrySet()) {
            try {
               electrodeBridge.registerRequestHandler(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, "Failed registering queued request handler registration");
            }
        }
        mQueuedRequestHandlersRegistration.clear();
    }

    private static void registerQueuedEventListeners() {
        for (Map.Entry<String, ElectrodeBridgeEventListener> entry : mQueuedEventListenersRegistration.entrySet()) {
            try {
                electrodeBridge.addEventListener(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, "Failed registering queued event listener registration");
            }
        }
        mQueuedEventListenersRegistration.clear();
    }

    private static void sendQueuedRequests() {
        for (Map.Entry<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener> entry : mQueuedRequests.entrySet()) {
            try {
                electrodeBridge.sendRequest(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, "Failed sending queued request");
            }
        }
        mQueuedRequests.clear();
    }

    private static void emitQueuedEvents() {
        for (ElectrodeBridgeEvent event : mQueuedEvents) {
            try {
                electrodeBridge.emitEvent(event);
            } catch (Exception e) {
                Log.e(TAG, "Failed sending queued event");
            }
        }
        mQueuedEvents.clear();
    }

}
