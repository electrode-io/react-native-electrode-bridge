package com.walmartlabs.electrode.reactnative.bridge;

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
public class ElectrodeBridge {

    private static final String TAG = ElectrodeBridge.class.getSimpleName();

    private static boolean isReactNativeReady;

    // We queue requests/events as long as react native initialization is not complete.
    // Indeed, if a client of the bridge calls `sendRequest` upon it's application start,
    // it will throw an exception due to the fact that react native initialization is not
    // complete (react native bridge not ready). RN initialization is asynchronous.
    // Doing this greatly simplifies things for the electrode bridge client as he does not
    // have to bother with burdensome code to wait for RN to be ready. We take care of that !
    // This solution does not really scale in the sense that if the user sends a 1000 requests
    // upon native app start, it can become problematic. But I don't see why a user would do that
    // unless it's a bug in its app
    private static final HashMap<String, RequestDispatcherImpl.RequestHandler> mQueuedRequestHandlersRegistration = new HashMap<>();
    private static final HashMap<String, EventDispatcherImpl.EventListener> mQueuedEventListenersRegistration = new HashMap<>();
    private static final HashMap<ElectrodeBridgeRequest, RequestCompletionListener> mQueuedRequests = new HashMap<>();
    private static final List<ElectrodeBridgeEvent> mQueuedEvents = new ArrayList<>();

    static {
        ElectrodeBridgeInternal.registerReactNativeReadyListener(new ElectrodeBridgeInternal.ReactNativeReadyListener() {
            @Override
            public void onReactNativeReady() {
                isReactNativeReady = true;
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

        ElectrodeBridgeInternal.instance().emitEvent(event);
    }

    /**
     * Sends a request
     *
     * @param request            The request to send
     * @param completionListener Listener to be called upon request completion
     */
    @SuppressWarnings("unused")
    public static void sendRequest(
            @NonNull ElectrodeBridgeRequest request,
            @NonNull final RequestCompletionListener completionListener) {
        if (!isReactNativeReady) {
            Log.d(TAG, "Queuing request. Will send once react native initialization is complete.");
            mQueuedRequests.put(request, completionListener);
            return;
        }

        ElectrodeBridgeInternal.instance().sendRequest(request, completionListener);
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
                                              @NonNull RequestDispatcherImpl.RequestHandler requestHandler) {
        if (!isReactNativeReady) {
            Log.d(TAG, "Queuing request handler registration. Will register once react native initialization is complete.");
            mQueuedRequestHandlersRegistration.put(name, requestHandler);
            return;
        }

        ElectrodeBridgeInternal.instance().requestRegistrar().registerRequestHandler(name, requestHandler);
    }

    /**
     * Registers an event listener
     *
     * @param name          The event name this listener is interested in
     * @param eventListener The event listener
     * @return A UUID to pass back to unregisterEventListener
     */
    @SuppressWarnings("unused")
    public static void registerEventListener(@NonNull String name,
                                             @NonNull EventDispatcherImpl.EventListener eventListener) {
        if (!isReactNativeReady) {
            Log.d(TAG, "Queuing event handler registration. Will register once react native initialization is complete.");
            mQueuedEventListenersRegistration.put(name, eventListener);
            return;
        }

        ElectrodeBridgeInternal.instance().eventRegistrar().registerEventListener(name, eventListener);
    }

    private static void registerQueuedRequestHandlers() {
        for (Map.Entry<String, RequestDispatcherImpl.RequestHandler> entry : mQueuedRequestHandlersRegistration.entrySet()) {
            try {
                ElectrodeBridgeInternal.instance().requestRegistrar().registerRequestHandler(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, "Failed registering queued request handler registration");
            }
        }
        mQueuedRequestHandlersRegistration.clear();
    }

    private static void registerQueuedEventListeners() {
        for (Map.Entry<String, EventDispatcherImpl.EventListener> entry : mQueuedEventListenersRegistration.entrySet()) {
            try {
                ElectrodeBridgeInternal.instance().eventRegistrar().registerEventListener(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, "Failed registering queued event listener registration");
            }
        }
        mQueuedEventListenersRegistration.clear();
    }

    private static void sendQueuedRequests() {
        for (Map.Entry<ElectrodeBridgeRequest, RequestCompletionListener> entry : mQueuedRequests.entrySet()) {
            try {
                ElectrodeBridgeInternal.instance().sendRequest(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                Log.e(TAG, "Failed sending queued request");
            }
        }
        mQueuedRequests.clear();
    }

    private static void emitQueuedEvents() {
        for (ElectrodeBridgeEvent event : mQueuedEvents) {
            try {
                ElectrodeBridgeInternal.instance().emitEvent(event);
            } catch (Exception e) {
                Log.e(TAG, "Failed sending queued event");
            }
        }
        mQueuedEvents.clear();
    }

}
