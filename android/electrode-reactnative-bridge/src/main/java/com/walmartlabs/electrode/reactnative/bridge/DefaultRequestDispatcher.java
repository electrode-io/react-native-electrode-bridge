package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

@SuppressWarnings("unused")
public class DefaultRequestDispatcher implements ElectrodeBridge.RequestDispatcher {
    private static final WritableMap EMPTY_WRITABLE_MAP = Arguments.createMap();

    private final RequestRegistrar<RequestHandler> mRequestRegistrar;

    /**
     * Initialize a new DefaultRequestDispatcher instance
     *
     * @param requestRegistrar The request registrar to use for this dispatcher
     */
    public DefaultRequestDispatcher(RequestRegistrar<RequestHandler> requestRegistrar) {
        mRequestRegistrar = requestRegistrar;
    }

    /**
     * Provide methods to report request completion
     */
    public interface RequestCompletion {
        /**
         * Error response
         *
         * @param message The error message
         */
        void error(@NonNull String message);

        /**
         * Successful response
         *
         * @param bundle A bundle containing the response data
         */
        void success(Bundle bundle);

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
         * @param requestCompletion An instance of RequestCompletion
         */
        void onRequest(@NonNull Bundle payload, @NonNull RequestCompletion requestCompletion);
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
        mRequestRegistrar.getRequestHandler(type).onRequest(payloadBundle, new RequestCompletion() {
            @Override
            public void error(@NonNull String message) {
                promise.reject(message);
            }

            @Override
            public void success(@NonNull Bundle bundle) {
                promise.resolve(Arguments.fromBundle(bundle));
            }

            @Override
            public void success() {
                promise.resolve(EMPTY_WRITABLE_MAP);
            }
        });
    }
}
