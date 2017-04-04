package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provide method to be notified of incoming request.
 * An implementor of this interface is expected to handle any incoming request and provide a {@link ElectrodeBridgeResponseListener#onSuccess(Object)} or {@link ElectrodeBridgeResponseListener#onFailure(FailureMessage)} response.
 */
public interface ElectrodeBridgeRequestHandler<TReq, TResp> {
    /**
     * Called whenever a request matching this handler is received
     *
     * @param payload          The payload of the request, payload can be null for a request.
     * @param responseListener An instance of {@link ElectrodeBridgeResponseListener}
     */
    void onRequest(@Nullable TReq payload, @NonNull ElectrodeBridgeResponseListener<TResp> responseListener);
}
