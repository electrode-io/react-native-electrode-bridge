package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BridgeTransaction {

    private final ElectrodeBridgeRequest request;
    private final ElectrodeBridgeResponseListener<Bundle> finalResponseListener;
    private ElectrodeBridgeResponse response;


    public BridgeTransaction(@NonNull ElectrodeBridgeRequest request, @Nullable ElectrodeBridgeResponseListener<Bundle> responseListener) {
        if (request.getType() != BridgeMessage.Type.REQUEST) {
            throw new IllegalArgumentException("BridgeTransaction constrictor expects a request type, did you accidentally pass in a different type(" + request.getType() + ") ? ");
        }
        this.request = request;
        this.finalResponseListener = responseListener;
    }


    public void setResponse(@NonNull ElectrodeBridgeResponse response) {
        this.response = response;
    }

    @NonNull
    public ElectrodeBridgeRequest getRequest() {
        return request;
    }

    @Nullable
    public ElectrodeBridgeResponseListener<Bundle> getFinalResponseListener() {
        return finalResponseListener;
    }

    @Nullable
    public ElectrodeBridgeResponse getResponse() {
        return response;
    }

    @NonNull
    public String getId() {
        return request.getId();
    }

    public boolean isJsInitiated() {
        return request.isJsInitiated();
    }
}
