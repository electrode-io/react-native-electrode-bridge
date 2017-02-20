package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Provide methods to report request completion
 */
public interface ElectrodeBridgeResponseListener {
    /**
     * Error response
     *
     * @param code    The error code
     * @param message The error message
     */
    void error(@NonNull String code, @NonNull String message);

    /**
     * Successful response
     *
     * @param bundle A bundle containing the response data
     */
    void success(@NonNull Bundle bundle);

    /**
     * Successful response
     */
    void success();
}
