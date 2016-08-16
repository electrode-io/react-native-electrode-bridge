package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

@SuppressWarnings("unused")
public class RequestDispatcherImpl implements ElectrodeBridge.RequestDispatcher {
    private static final Bundle EMPTY_BUNDLE = new Bundle();

    private final RequestRegistrar<RequestHandler> mRequestRegistrar;

    /**
     * Initialize a new RequestDispatcherImpl instance
     *
     * @param requestRegistrar The request registrar to use for this dispatcher
     */
    public RequestDispatcherImpl(RequestRegistrar<RequestHandler> requestRegistrar) {
        mRequestRegistrar = requestRegistrar;
    }

    /**
     * Provide methods to report request completion
     */
    public interface RequestCompletioner {
        /**
         * Error response
         *
         * @param code The error code
         * @param message The error message
         */
        void error(@NonNull String code, @NonNull String message);

        /**
         * Successful response
         *
         * @param bundle A bundle containing the response data
         */
        void success(@NonNull Bundle bundle);

        /**
         * Successful response
         */
        void success();
    }

    /**
     * Provide method to be notified of incoming request
     */
    public interface RequestHandler {
        /**
         * Called whenever a request matching this handler is received
         *
         * @param payload The payload of the request as a Bundle
         * @param requestCompletioner An instance of RequestCompletioner
         */
        void onRequest(@NonNull Bundle payload, @NonNull RequestCompletioner requestCompletioner);
    }

    /**
     * Dispatch a request
     *
     * @param type The type of the request to dispatch
     * @param id The id of the request
     * @param payload The payload of the request as a ReadableMap
     * @param promise A promise to fulfil upon request completion
     */
    @Override
    public void dispatchRequest(@NonNull String type,
                                @NonNull String id,
                                @NonNull ReadableMap payload,
                                @NonNull final Promise promise) {
        Bundle payloadBundle = Arguments.toBundle(payload);

        RequestHandler requestHandler = mRequestRegistrar.getRequestHandler(type);

        if (requestHandler == null) {
            promise.reject("ENOHANDLER", "No registered request handler for type " + type);
            return;
        }

        requestHandler.onRequest((payloadBundle != null ? payloadBundle : EMPTY_BUNDLE),
                new RequestCompletioner() {
            @Override
            public void error(@NonNull String code, @NonNull String message) {
                promise.reject(code, message);
            }

            @Override
            public void success(@NonNull Bundle bundle) {
                promise.resolve(Arguments.fromBundle(bundle));
            }

            @Override
            public void success() {
                promise.resolve(Arguments.createMap());
            }
        });
    }
}
