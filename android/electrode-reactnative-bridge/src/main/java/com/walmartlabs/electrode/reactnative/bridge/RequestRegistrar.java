package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

public interface RequestRegistrar<T> {

    /**
     * Registers a request handler
     *
     * @param name The request name this handler can handle
     * @param requestHandler The request handler instance
     * @return UUID to provide when calling unregisterRequestHandler
     */
    @NonNull
    UUID registerRequestHandler(@NonNull String name, @NonNull T requestHandler);

    /**
     * Unregisters a request handler
     *
     * @param requestHandlerUuid The UUID that was obtained through initial registerRequestHandler
     * call
     */
    void unregisterRequestHandler(@NonNull UUID requestHandlerUuid);

    /**
     * Gets the request handler registered for a given request name
     *
     * @param name The request name
     * @return The request handler instance that can handle this request name or null if no such
     * request handler was registered
     */
    @Nullable
    T getRequestHandler(@NonNull String name);
}
