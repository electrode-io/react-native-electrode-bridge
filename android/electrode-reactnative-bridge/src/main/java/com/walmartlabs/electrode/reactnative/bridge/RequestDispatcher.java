package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface RequestDispatcher {

    void dispatchRequest(@NonNull final ElectrodeBridgeRequest bridgeRequest, @NonNull final ElectrodeBridgeResponseListener<Bundle> responseListener);

    /**
     * Checks to see if a request handler is available for given request
     *
     * @param name
     * @return true | false
     */
    boolean canHandleRequest(@NonNull String name);
}
