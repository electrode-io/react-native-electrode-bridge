package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Bridge failure message interface.
 */

public interface FailureMessage {

    /**
     * Error code
     *
     * @return String
     */
    @NonNull
    String getCode();

    /**
     * Error message, a user displayable error message.
     *
     * @return String
     */
    @NonNull
    String getMessage();


    /**
     * Optional exception that can be passed along to describe the failure.
     *
     * @return Exception
     */
    @SuppressWarnings("unused")
    @Nullable
    Throwable getException();


    /**
     * Optional debug message mainly used for debugging purpose. Provides insights into the failure.
     *
     * @return String
     */
    @SuppressWarnings("unused")
    @Nullable
    String getDebugMessage();
}
