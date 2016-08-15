package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

public interface RequestRegistrar<T> {

    /**
     * Registers a request handler
     *
     * @param type The type of request this handler can handle
     * @param requestHandler The request handler instance
     * @return UUID to provide when calling unregisterRequestHandler
     */
    @NonNull
    UUID registerRequestHandler(@NonNull String type, @NonNull T requestHandler)
            throws ExistingHandlerException;

    /**
     * Unregisters a request handler
     *
     * @param requestHandlerUuid The UUID that was obtained through initial registerRequestHandler
     * call
     */
    void unregisterRequestHandler(@NonNull UUID requestHandlerUuid);

    /**
     * Gets the request handler registered for a given request type
     *
     * @param type The type of request
     * @return The request handler instance that can handle this request type or null if no such
     * request handler was registered
     */
    @Nullable
    T getRequestHandler(@NonNull String type);
}
