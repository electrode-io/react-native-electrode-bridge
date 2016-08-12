package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRequestRegistrar<T> implements RequestRegistrar<T> {

    private final ConcurrentHashMap<UUID, String> mRequestTypeByUUID = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, T> mRequestHandlerByRequestType = new ConcurrentHashMap<>();

    /**
     * Registers a request handler
     *
     * @param type The type of request this handler can handle
     * @param requestHandler The request handler instance
     * @return UUID to provide when calling unregisterRequestHandler
     */
    @SuppressWarnings("unused")
    public UUID registerRequestHandler(@NonNull String type, @NonNull T requestHandler)
            throws ExistingHandlerException {
        if (mRequestHandlerByRequestType.containsKey(type)) {
            throw new ExistingHandlerException(
                    String.format("A request handler has already been registered for type %s", type));
        }
        UUID requestHandlerUuid = UUID.randomUUID();
        mRequestHandlerByRequestType.put(type, requestHandler);
        mRequestTypeByUUID.put(requestHandlerUuid, type);
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
        String requestType = mRequestTypeByUUID.remove(requestHandlerUuid);
        if (requestType != null) {
            mRequestHandlerByRequestType.remove(requestType);
        }
    }

    /**
     * Gets the request handler registered for a given request type
     *
     * @param type The type of request
     * @return The request handler instance that can handle this request type or null if no such
     * request handler was registered
     */
    @Nullable
    public T getRequestHandler(String type) {
        return mRequestHandlerByRequestType.get(type);
    }
}
