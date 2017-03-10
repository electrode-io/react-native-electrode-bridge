package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

public interface RequestDispatcher {

    void dispatchRequest(@NonNull final ElectrodeBridgeRequest bridgeRequest, @NonNull final ElectrodeBridgeResponseListener<Object> responseListener);

    /**
     * Checks to see if a request handler is available for given request
     *
     * @param name {@link String} request name
     * @return true | false
     */
    boolean canHandleRequest(@NonNull String name);
}
