package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

class RequestDispatcherImpl implements RequestDispatcher {
    private static final String TAG = RequestDispatcherImpl.class.getSimpleName();

    private final RequestRegistrar<ElectrodeBridgeRequestHandler<Bundle, Bundle>> mRequestRegistrar;

    /**
     * Initialize a new RequestDispatcherImpl instance
     *
     * @param requestRegistrar The request registrar to use for this dispatcher
     */
    RequestDispatcherImpl(@NonNull RequestRegistrar<ElectrodeBridgeRequestHandler<Bundle, Bundle>> requestRegistrar) {
        mRequestRegistrar = requestRegistrar;
    }

    @Override
    public void dispatchRequest(@NonNull String requestName, @NonNull final String requestId, @Nullable Bundle requestData, @NonNull final Promise callBackPromise, final boolean isJs) {
        Logger.d(TAG, "dispatching request(id=%s) locally, with promise(%s)", requestId, callBackPromise);
        ElectrodeBridgeRequestHandler<Bundle, Bundle> requestHandler = mRequestRegistrar.getRequestHandler(requestName);
        if (requestHandler == null) {
            callBackPromise.reject("ENOHANDLER", "No registered request handler for request name " + requestName);
            return;
        }

        requestHandler.onRequest(requestData,
                new ElectrodeBridgeResponseListener<Bundle>() {
                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        Logger.d(TAG, "resolving FAILED request(id=%s),  promise(%s), failureMessage(%s)", requestId, callBackPromise, failureMessage);
                        callBackPromise.reject(failureMessage.getCode(), failureMessage.getMessage());
                    }

                    @Override
                    public void onSuccess(@Nullable Bundle bundle) {
                        Logger.d(TAG, "resolving SUCCESSFUL request(id=%s),  promise(%s), responseBundle(%s), isJS(%s)", requestId, callBackPromise, bundle, isJs);
                        bundle = bundle != null ? bundle : Bundle.EMPTY;
                        if (isJs) {
                            callBackPromise.resolve(Arguments.fromBundle(bundle));
                        } else {
                            callBackPromise.resolve(bundle);
                        }
                    }
                });
    }

    @Override
    public boolean canHandleRequest(@NonNull String name) {
        return mRequestRegistrar.getRequestHandler(name) != null;
    }
}
