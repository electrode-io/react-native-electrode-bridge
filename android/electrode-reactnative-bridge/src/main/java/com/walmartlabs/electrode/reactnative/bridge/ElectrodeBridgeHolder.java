package com.walmartlabs.electrode.reactnative.bridge;

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

    private static ElectrodeNativeBridge electrodeNativeBridge;

    // We queue requests/events as long as react native initialization is not complete.
    // Indeed, if a client of the bridge calls `sendRequest` upon it's application start,
    // it will throw an exception due to the fact that react native initialization is not
    // complete (react native bridge not ready). RN initialization is asynchronous.
    // Doing this greatly simplifies things for the electrode bridge client as he does not
    // have to bother with burdensome code to wait for RN to be ready. We take care of that !
    // This solution does not really scale in the sense that if the user sends a 1000 requests
    // upon native app start, it can become problematic. But I don't see why a user would do that
    // unless it's a bug in its app
    private static final HashMap<String, ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object>> mQueuedRequestHandlersRegistration = new HashMap<>();
    private static final HashMap<String, ElectrodeBridgeEventListener<ElectrodeBridgeEvent>> mQueuedEventListenersRegistration = new HashMap<>();
    private static final HashMap<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener<ElectrodeBridgeResponse>> mQueuedRequests = new HashMap<>();
    private static final List<ElectrodeBridgeEvent> mQueuedEvents = new ArrayList<>();
    private static List<ConstantsProvider> constantsProviders = new ArrayList<>();

    static {
        ElectrodeBridgeTransceiver.registerReactNativeReadyListener(new ElectrodeBridgeTransceiver.ReactNativeReadyListener() {
            @Override
            public void onReactNativeReady() {
                isReactNativeReady = true;
                electrodeNativeBridge = ElectrodeBridgeTransceiver.instance();
                registerQueuedEventListeners();
                registerQueuedRequestHandlers();
                sendQueuedRequests();
                emitQueuedEvents();
                addConstantProviders();
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

        electrodeNativeBridge.sendEvent(event);
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
            @NonNull final ElectrodeBridgeResponseListener<ElectrodeBridgeResponse> responseListener) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing request(%s). Will send once react native initialization is complete.", request);
            mQueuedRequests.put(request, responseListener);
            return;
        }

        electrodeNativeBridge.sendRequest(request, responseListener);
    }

    /**
     * Registers a request handler
     *
     * @param name           The request name this handler can handle
     * @param requestHandler The request handler instance
     */
    public static void registerRequestHandler(@NonNull String name,
                                              @NonNull ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing request handler registration for request(name=%s). Will register once react native initialization is complete.", name);
            mQueuedRequestHandlersRegistration.put(name, requestHandler);
            return;
        }

        electrodeNativeBridge.registerRequestHandler(name, requestHandler);
    }

    /**
     * Registers an event listener
     *
     * @param name          The event name this listener is interested in
     * @param eventListener The event listener
     */
    public static void addEventListener(@NonNull String name,
                                        @NonNull ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener) {
        if (!isReactNativeReady) {
            Logger.d(TAG, "Queuing event handler registration for event(name=%s). Will register once react native initialization is complete.", name);
            mQueuedEventListenersRegistration.put(name, eventListener);
            return;
        }

        electrodeNativeBridge.addEventListener(name, eventListener);
    }

    public static void addConstantsProvider(@NonNull ConstantsProvider constantsProvider) {
        if (!isReactNativeReady) {
            ElectrodeBridgeHolder.constantsProviders.add(constantsProvider);
            return;
        }
        electrodeNativeBridge.addConstantsProvider(constantsProvider);
    }

    private static void registerQueuedRequestHandlers() {
        for (Map.Entry<String, ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object>> entry : mQueuedRequestHandlersRegistration.entrySet()) {
            electrodeNativeBridge.registerRequestHandler(entry.getKey(), entry.getValue());
        }
        mQueuedRequestHandlersRegistration.clear();
    }

    private static void registerQueuedEventListeners() {
        for (Map.Entry<String, ElectrodeBridgeEventListener<ElectrodeBridgeEvent>> entry : mQueuedEventListenersRegistration.entrySet()) {
            electrodeNativeBridge.addEventListener(entry.getKey(), entry.getValue());
        }
        mQueuedEventListenersRegistration.clear();
    }

    private static void sendQueuedRequests() {
        for (Map.Entry<ElectrodeBridgeRequest, ElectrodeBridgeResponseListener<ElectrodeBridgeResponse>> entry : mQueuedRequests.entrySet()) {
            electrodeNativeBridge.sendRequest(entry.getKey(), entry.getValue());
        }
        mQueuedRequests.clear();
    }

    private static void emitQueuedEvents() {
        for (ElectrodeBridgeEvent event : mQueuedEvents) {
            electrodeNativeBridge.sendEvent(event);
        }
        mQueuedEvents.clear();
    }

    private static void addConstantProviders() {
        if (constantsProviders != null) {
            for (ConstantsProvider provider : constantsProviders) {
                electrodeNativeBridge.addConstantsProvider(provider);
            }
        }
        constantsProviders.clear();
    }

}
