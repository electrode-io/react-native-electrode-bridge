package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRegistrarImpl<T> implements RequestRegistrar<T> {

    private final ConcurrentHashMap<UUID, String> mRequestNameByUUID = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, T> mRequestHandlerByRequestName = new ConcurrentHashMap<>();

    /**
     * Registers a request handler
     *
     * @param name The request name this handler can handle
     * @param requestHandler The request handler instance
     * @return UUID to provide when calling unregisterRequestHandler
     */
    @SuppressWarnings("unused")
    @NonNull
    public UUID registerRequestHandler(@NonNull String name, @NonNull T requestHandler)
            throws ExistingHandlerException {
        if (mRequestHandlerByRequestName.containsKey(name)) {
            throw new ExistingHandlerException(
                    String.format("A request handler has already been registered for name %s", name));
        }
        UUID requestHandlerUuid = UUID.randomUUID();
        mRequestHandlerByRequestName.put(name, requestHandler);
        mRequestNameByUUID.put(requestHandlerUuid, name);
        return requestHandlerUuid;
    }

    /**
     * Unregisters a request handler
     *
     * @param requestHandlerUuid The UUID that was obtained through initial registerRequestHandler
     * call
     */
    @SuppressWarnings("unused")
    public void unregisterRequestHandler(@NonNull UUID requestHandlerUuid) {
        String requestName = mRequestNameByUUID.remove(requestHandlerUuid);
        if (requestName != null) {
            mRequestHandlerByRequestName.remove(requestName);
        }
    }

    /**
     * Gets the request handler registered for a given request name
     *
     * @param name The name of request
     * @return The request handler instance that can handle this request name or null if no such
     * request handler was registered
     */
    @Nullable
    public T getRequestHandler(@NonNull String name) {
        return mRequestHandlerByRequestName.get(name);
    }
}
