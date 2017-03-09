package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

class RequestDispatcherImpl implements RequestDispatcher {
    private static final String TAG = RequestDispatcherImpl.class.getSimpleName();

    private final RequestRegistrar<ElectrodeBridgeRequestHandler<Bundle, Object>> mRequestRegistrar;

    /**
     * Initialize a new RequestDispatcherImpl instance
     *
     * @param requestRegistrar The request registrar to use for this dispatcher
     */
    RequestDispatcherImpl(@NonNull RequestRegistrar<ElectrodeBridgeRequestHandler<Bundle, Object>> requestRegistrar) {
        mRequestRegistrar = requestRegistrar;
    }

    @Override
    public void dispatchRequest(@NonNull final ElectrodeBridgeRequest bridgeRequest, @NonNull final ElectrodeBridgeResponseHandler responseHandler) {
        final String requestId = bridgeRequest.getId();
        final String requestName = bridgeRequest.getName();

        Logger.d(TAG, "dispatching request(id=%s) locally", requestId);
        ElectrodeBridgeRequestHandler<Bundle, Object> requestHandler = mRequestRegistrar.getRequestHandler(requestName);
        if (requestHandler == null) {
            FailureMessage failureMessage = BridgeFailureMessage.create("ENOHANDLER", "No registered request handler for request name " + requestName);
            responseHandler.onResponse(ElectrodeBridgeResponse.createResponseForRequest(bridgeRequest, null, failureMessage));
            return;
        }

        requestHandler.onRequest(bridgeRequest.bundle(),
                new ElectrodeBridgeResponseListener<Object>() {
                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        Logger.d(TAG, "resolving FAILED request(id=%s),  failureMessage(%s)", requestId, failureMessage);
                        ElectrodeBridgeResponse response = ElectrodeBridgeResponse.createResponseForRequest(bridgeRequest, null, failureMessage);
                        responseHandler.onResponse(response);
                    }

                    @Override
                    public void onSuccess(@Nullable Object data) {
                        Logger.d(TAG, "resolving SUCCESSFUL request(id=%s), responseObj(%s)", requestId, data);
                        ElectrodeBridgeResponse response = ElectrodeBridgeResponse.createResponseForRequest(bridgeRequest, data, null);
                        responseHandler.onResponse(response);
                    }
                });
    }


    @Override
    public boolean canHandleRequest(@NonNull String name) {
        return mRequestRegistrar.getRequestHandler(name) != null;
    }
}
