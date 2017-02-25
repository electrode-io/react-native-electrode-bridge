package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

@SuppressWarnings("unused")
public class RequestDispatcherImpl implements ElectrodeBridgeInternal.RequestDispatcher {
    private static final String TAG = RequestDispatcherImpl.class.getSimpleName();
    private static final Bundle EMPTY_BUNDLE = new Bundle();

    private final RequestRegistrar<ElectrodeBridgeRequestHandler> mRequestRegistrar;

    /**
     * Initialize a new RequestDispatcherImpl instance
     *
     * @param requestRegistrar The request registrar to use for this dispatcher
     */
    public RequestDispatcherImpl(RequestRegistrar<ElectrodeBridgeRequestHandler> requestRegistrar) {
        mRequestRegistrar = requestRegistrar;
    }

    @Override
    public void dispatchRequest(@NonNull String name, @NonNull final String id, @NonNull Bundle data, @NonNull final Promise promise) {
        Logger.d(TAG, "dispatching request(id=%s) locally, with promise(%s)", id, promise);
        ElectrodeBridgeRequestHandler requestHandler = mRequestRegistrar.getRequestHandler(name);
        if (requestHandler == null) {
            promise.reject("ENOHANDLER", "No registered request handler for request name " + name);
            return;
        }

        requestHandler.onRequest(data,
                new ElectrodeBridgeResponseListener<Bundle>() {
                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        Logger.d(TAG, "resolving FAILED request(id=%s),  promise(%s), failureMessage(%s)", id, promise, failureMessage);
                        promise.reject(failureMessage.getCode(), failureMessage.getMessage());
                    }

                    @Override
                    public void onSuccess(@NonNull Bundle bundle) {
                        Logger.d(TAG, "resolving SUCCESSFUL request(id=%s),  promise(%s), responseBundle(%s)", id, promise, bundle);
                        promise.resolve(bundle);
                    }
                });
    }

  @Override
  public void dispatchJSOriginatingRequest(@NonNull String name, @NonNull final String id, @NonNull Bundle data, @NonNull final Promise promise) {
    Logger.d(TAG, "dispatching request(id=%s) locally, with promise(%s)", id, promise);
    ElectrodeBridgeRequestHandler requestHandler = mRequestRegistrar.getRequestHandler(name);
    if (requestHandler == null) {
      promise.reject("ENOHANDLER", "No registered request handler for request name " + name);
      return;
    }

    requestHandler.onRequest(data,
            new ElectrodeBridgeResponseListener<Bundle>() {
              @Override
              public void onFailure(@NonNull FailureMessage failureMessage) {
                Logger.d(TAG, "resolving FAILED request(id=%s),  promise(%s), failureMessage(%s)", id, promise, failureMessage);
                promise.reject(failureMessage.getCode(), failureMessage.getMessage());
              }

              @Override
              public void onSuccess(@NonNull Bundle bundle) {
                Logger.d(TAG, "resolving SUCCESSFUL request(id=%s),  promise(%s), responseBundle(%s)", id, promise, bundle);
                promise.resolve(Arguments.fromBundle(bundle));
              }
            });
  }

  @Override
  public boolean canHandleRequest(@NonNull String name) {
    return mRequestRegistrar.getRequestHandler(name) != null;
  }
}
