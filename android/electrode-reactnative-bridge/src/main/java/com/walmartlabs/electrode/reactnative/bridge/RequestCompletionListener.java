package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Provides methods to be informed of a request completion state
 */
public interface RequestCompletionListener {
    /**
     * Called if request was successful
     *
     * @param payload The response payload as a bundle (empty bundle if no data)
     */
    void onSuccess(@NonNull Bundle payload);

    /**
     * Called if request failed
     *
     * @param code    The error code ("EUNKNOWN" if no error code was set)
     * @param message The error message
     */
    void onError(@NonNull String code, @NonNull String message);
}