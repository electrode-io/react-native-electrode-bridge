package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

public interface RequestRegistrar<T> {

    /**
     * Registers a request handler
     *
     * @param name           The request name this handler can handle
     * @param requestHandler The request handler instance
     * @param uuid           {@link UUID} of {@code requestHandler}
     * @return Returns true if the {@code requestHandler} is registered
     */
    @NonNull
    boolean registerRequestHandler(@NonNull String name, @NonNull T requestHandler, @NonNull UUID uuid);

    /**
     * Unregisters a request handler
     *
     * @param requestHandlerUuid {@link UUID} of registerRequestHandler
     * @return registerRequestHandler unregistered
     */
    @SuppressWarnings("unused")
    T unregisterRequestHandler(@NonNull UUID requestHandlerUuid);

    /**
     * Gets the request handler registered for a given request name
     *
     * @param name The request name
     * @return The request handler instance that can handle this request name or null if no such
     * request handler was registered
     */
    @Nullable
    T getRequestHandler(@NonNull String name);

    /**
     * Query UUID of the request handler
     *
     * @param name
     * @return {@link UUID} of the request handler
     */
    @NonNull
    UUID getRequestHandlerId(@NonNull String name);
}
