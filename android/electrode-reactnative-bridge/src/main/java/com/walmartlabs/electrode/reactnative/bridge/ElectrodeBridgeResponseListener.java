package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

/**
 * Provide methods to report response for a request.
 */
public interface ElectrodeBridgeResponseListener<T> {
    /**
     * Error response
     *
     * @param code    The error code
     * @param message The error message
     */
    void onFailure(@NonNull String code, @NonNull String message);

    /**
     * Successful response
     *
     * @param responseData response object{@link T}
     */
    void onSuccess(@NonNull T responseData);
}
