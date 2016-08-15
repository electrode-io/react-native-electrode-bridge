package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

/**
 * Exception thrown when trying to register a request handler for a request type that already
 * has an associated registered request handler
 */
public class ExistingHandlerException extends Exception {
    public ExistingHandlerException(@NonNull String message) {
        super(message);
    }
}
