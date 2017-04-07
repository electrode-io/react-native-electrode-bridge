package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

class RequestDispatcherImpl implements RequestDispatcher {
    private static final String TAG = RequestDispatcherImpl.class.getSimpleName();

    private final RequestRegistrar<ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object>> mRequestRegistrar;

    /**
     * Initialize a new RequestDispatcherImpl instance
     *
     * @param requestRegistrar The request registrar to use for this dispatcher
     */
    RequestDispatcherImpl(@NonNull RequestRegistrar<ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object>> requestRegistrar) {
        mRequestRegistrar = requestRegistrar;
    }

    @Override
    public void dispatchRequest(@NonNull final ElectrodeBridgeRequest bridgeRequest, @NonNull final ElectrodeBridgeResponseListener<Object> responseListener) {
        final String requestId = bridgeRequest.getId();
        final String requestName = bridgeRequest.getName();

        Logger.d(TAG, "dispatching request(id=%s) locally", requestId);
        ElectrodeBridgeRequestHandler<ElectrodeBridgeRequest, Object> requestHandler = mRequestRegistrar.getRequestHandler(requestName);
        if (requestHandler == null) {
            FailureMessage failureMessage = BridgeFailureMessage.create("ENOHANDLER", "No registered request handler for request name " + requestName);
            responseListener.onFailure(failureMessage);
            return;
        }
        requestHandler.onRequest(bridgeRequest,responseListener);
    }


    @Override
    public boolean canHandleRequest(@NonNull String name) {
        return mRequestRegistrar.getRequestHandler(name) != null;
    }
}
