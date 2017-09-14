package com.walmartlabs.electrode.reactnative.bridge;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequest;
import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeRequestHandler;

import java.util.UUID;

/**
 * Place holder to set request information during deferred registration
 */

public class RequestHandlerPlaceholder {
    private UUID mUUID;
    private ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> mRequestHandler;

    public RequestHandlerPlaceholder(UUID uuid, ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler) {
        this.mUUID = uuid;
        this.mRequestHandler = requestHandler;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> getRequestHandler() {
        return mRequestHandler;
    }
}
