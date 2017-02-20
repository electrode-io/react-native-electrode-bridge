package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Provide method to be notified of incoming request. An implementor of this interface is expected to handle any incoming request and provide a success or failure response.
 */
public interface ElectrodeBridgeRequestHandler {
    /**
     * Called whenever a request matching this handler is received
     *
     * @param payload             The payload of the request as a Bundle
     * @param requestCompletioner An instance of RequestCompletioner
     */
    void onRequest(@NonNull Bundle payload, @NonNull RequestDispatcherImpl.RequestCompletioner requestCompletioner);
}
