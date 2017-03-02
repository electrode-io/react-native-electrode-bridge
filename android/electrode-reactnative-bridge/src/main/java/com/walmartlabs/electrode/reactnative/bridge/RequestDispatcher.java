package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Promise;

public interface RequestDispatcher {
    /**
     * Dispatch a request to the handler registered on native side.
     *
     * @param requestName     The name of the request to dispatch
     * @param requestId       The request id
     * @param requestData     The data of the request as a ReadableMap
     * @param callBackPromise A promise to fulfil upon request completion
     * @param isJs            indicates if the request is initialized from JS side.
     */
    void dispatchRequest(@NonNull String requestName, @NonNull String requestId, @Nullable Bundle requestData, @NonNull Promise callBackPromise, boolean isJs);


    /**
     * Checks to see if a request handler is available for given request
     *
     * @param name
     * @return true | false
     */
    boolean canHandleRequest(@NonNull String name);
}
