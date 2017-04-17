package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Client facing bridge api. Defines all the actions that a native client can perform through the bridge.
 */

public interface ElectrodeNativeBridge {
    /**
     * Send a request from Android native to either Native or React Native side depending on where the request handler is registered.
     *
     * @param request          The ElectrodeBridgeRequest that will contain the request name,data, destination mode, and timeout
     * @param responseListener the response call back listener to issue success or failure of the {@param request}.
     */
    void sendRequest(@NonNull final ElectrodeBridgeRequest request, @NonNull final ElectrodeBridgeResponseListener<ElectrodeBridgeResponse> responseListener);

    /**
     * Registere the request handler, which will be used to handle any
     *
     * @param name           name of the request
     * @param requestHandler call back to be issued for a given request.
     */
    void registerRequestHandler(@NonNull String name, @NonNull ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler);

    /**
     * Sends an event with some data to the all the even listeners.
     *
     * @param event The event to emit
     */
    void sendEvent(@NonNull ElectrodeBridgeEvent event);

    /**
     * Adds an event listener for the passed event
     *
     * @param name          The event name this listener is interested in
     * @param eventListener The event listener
     * @return A UUID to pass back to unregisterEventListener
     */
    @NonNull
    UUID addEventListener(@NonNull String name, @NonNull ElectrodeBridgeEventListener<ElectrodeBridgeEvent> eventListener);

    /**
     * Registers the listen that will be used by the bridge module to get the constant values exposed to JavaScript
     *
     * @param constantsProvider
     */
    void addConstantsProvider(@NonNull ConstantsProvider constantsProvider);
}
