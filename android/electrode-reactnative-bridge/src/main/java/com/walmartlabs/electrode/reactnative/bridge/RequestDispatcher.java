package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Promise;

public interface RequestDispatcher {
    /**
     * Dispatch a request to the handler registered on native side.
     *
     * @param name    The name of the request to dispatch
     * @param id      The request id
     * @param data    The data of the request as a ReadableMap
     * @param promise A promise to fulfil upon request completion
     */
    void dispatchRequest(@NonNull String name, @NonNull String id, @Nullable Bundle data, @NonNull Promise promise);


    /**
     * Checks to see if a request handler is available for given request
     *
     * @param name
     * @return true | false
     */
    boolean canHandleRequest(@NonNull String name);
}
