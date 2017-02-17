package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.PromiseImpl;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ElectrodeBridgeInternal extends ReactContextBaseJavaModule {

    private static final String TAG = ElectrodeBridgeInternal.class.getSimpleName();

    private static final String BRIDGE_EVENT = "electrode.bridge.event";
    private static final String BRIDE_REQUEST = "electrode.bridge.request";
    private static final String BRIDGE_RESPONSE = "electrode.bridge.response";
    private static final String BRIDGE_RESPONSE_ERROR = "error";
    private static final String BRIDGE_RESPONSE_ERROR_CODE = "code";
    private static final String BRIDGE_RESPONSE_ERROR_MESSAGE = "message";
    private static final String BRIDGE_MSG_DATA = "data";
    private static final String BRIDGE_MSG_NAME = "name";
    private static final String BRIDGE_MSG_ID = "id";
    private static final String BRIDGE_REQUEST_ID = "requestId";
    private static final String UNKNOWN_ERROR_CODE = "EUNKNOWN";

    private final ReactContextWrapper mReactContextWrapper;
    private final EventDispatcher mEventDispatcher;
    private final RequestDispatcher mRequestDispatcher;

    // Singleton instance of the bridge
    private static ElectrodeBridgeInternal sInstance;

    private final ConcurrentHashMap<String, Promise> pendingPromiseByRequestId = new ConcurrentHashMap<>();
    private final EventRegistrar<EventDispatcherImpl.EventListener> mEventRegistrar = new EventRegistrarImpl<>();
    private final RequestRegistrar<RequestDispatcherImpl.RequestHandler> mRequestRegistrar = new RequestRegistrarImpl<>();

    private static boolean sIsReactNativeReady;

    /**
     * Initializes a new instance of ElectrodeBridgeInternal
     *
     * @param reactContextWrapper The react application context
     */
    private ElectrodeBridgeInternal(@NonNull ReactContextWrapper reactContextWrapper) {
        super(reactContextWrapper.getContext());
        mReactContextWrapper = reactContextWrapper;
        mEventDispatcher = new EventDispatcherImpl(mEventRegistrar);
        mRequestDispatcher = new RequestDispatcherImpl(mRequestRegistrar);
    }

    /**
     * Creates the ElectrodeBridgeInternal singleton
     *
     * @param reactApplicationContext The react application context
     * @return The singleton instance of ElectrodeBridgeInternal
     */
    public static ElectrodeBridgeInternal create(ReactApplicationContext reactApplicationContext) {
        return create(new ReactContextWrapperInternal(reactApplicationContext));
    }

    /**
     * Creates the ElectrodeBridgeInternal singleton
     *
     * @param reactContextWrapper
     * @return The singleton instance of ElectrodeBridgeInternal
     */
    @VisibleForTesting
    static ElectrodeBridgeInternal create(ReactContextWrapper reactContextWrapper) {
        Logger.d(TAG, "Creating ElectrodeBridgeInternal instance");
        synchronized (ElectrodeBridgeInternal.class) {
            if (sInstance == null) {
                sInstance = new ElectrodeBridgeInternal(reactContextWrapper);
            }
        }
        return sInstance;
    }

    /**
     * Returns the singleton instance of the bridge
     */
    public static ElectrodeBridgeInternal instance() {
        if (sInstance == null) {
            throw new IllegalStateException("Bridge singleton has not been created. Make sure to call create first.");
        }
        return sInstance;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "ElectrodeBridge";
    }

    /**
     * Provides a method to dispatch an event
     */
    public interface EventDispatcher {
        /**
         * Dispatch an event
         *
         * @param id   The event id
         * @param name The name of the event to dispatch
         * @param data The data of the event as a ReadableMap
         */
        void dispatchEvent(@NonNull String id,
                           @NonNull String name,
                           @NonNull ReadableMap data);

    }

    /**
     * Provides a method to dispatch a request
     */
    public interface RequestDispatcher {
        /**
         * Dispatch a request to the handler registered on native side.
         *
         * @param name    The name of the request to dispatch
         * @param id      The request id
         * @param data    The data of the request as a ReadableMap
         * @param promise A promise to fulfil upon request completion
         */
        void dispatchRequest(@NonNull String name,
                             @NonNull String id,
                             @NonNull Bundle data,
                             @NonNull Promise promise);
    }

    /**
     * @return The event listener register
     */
    public EventRegistrar<EventDispatcherImpl.EventListener> eventRegistrar() {
        return mEventRegistrar;
    }

    /**
     * @return The request handler registrar
     */
    public RequestRegistrar<RequestDispatcherImpl.RequestHandler> requestRegistrar() {
        return mRequestRegistrar;
    }

    /**
     * Emits an event with some data to the JS react native side
     *
     * @param event The event to emit
     */
    @SuppressWarnings("unused")
    public void emitEvent(@NonNull ElectrodeBridgeEvent event) {
        String id = getUUID();
        WritableMap message = buildMessage(id, event.getName(), Arguments.fromBundle(event.getData()));

        Log.d(TAG, String.format("Emitting event[name:%s id:%s]", event.getName(), id));

        if (event.getDispatchMode() == ElectrodeBridgeEvent.DispatchMode.JS) {
            mReactContextWrapper.emitEvent(BRIDGE_EVENT, message);
        } else if (event.getDispatchMode() == ElectrodeBridgeEvent.DispatchMode.NATIVE) {
            dispatchEvent(event.getName(), id, Arguments.fromBundle(event.getData()));
        } else if (event.getDispatchMode() == ElectrodeBridgeEvent.DispatchMode.GLOBAL) {
            mReactContextWrapper.emitEvent(BRIDGE_EVENT, message);
            dispatchEvent(event.getName(), id, Arguments.fromBundle(event.getData()));
        }
    }


    /**
     * Sends a request
     *
     * @param request            The request to send
     * @param completionListener Listener to be called upon request completion
     */
    @SuppressWarnings("unused")
    public void sendRequest(
            @NonNull ElectrodeBridgeRequest request,
            @NonNull final RequestCompletionListener completionListener) {
        final String id = getUUID();

        final Promise promise = new PromiseImpl(new Callback() {
            @Override
            public void invoke(final Object... args) {
                mReactContextWrapper.runOnUiQueueThread(new Runnable() {
                    @Override
                    public void run() {
                        ReadableMap data = (ReadableMap) args[0];
                        Bundle bundle = new Bundle();

                        if (data != null) {
                            switch (data.getType(BRIDGE_MSG_DATA)) {
                                case Array: {
                                    ReadableArray readableArray = data.getArray(BRIDGE_MSG_DATA);
                                    if (readableArray.size() != 0) {
                                        switch (readableArray.getType(0)) {
                                            case String:
                                                bundle.putStringArray("rsp", ArgumentsEx.toStringArray(readableArray));
                                                break;
                                            case Boolean:
                                                bundle.putBooleanArray("rsp", ArgumentsEx.toBooleanArray(readableArray));
                                                break;
                                            case Number:
                                                // Can be int or double
                                                bundle.putDoubleArray("rsp", ArgumentsEx.toDoubleArray(readableArray));
                                                break;
                                            case Map:
                                                bundle.putParcelableArray("rsp", ArgumentsEx.toBundleArray(readableArray));
                                                break;
                                            case Array:
                                                // Don't support array of arrays yet
                                                break;
                                        }
                                    }
                                }
                                break;
                                case Map:
                                    bundle.putBundle("rsp", ArgumentsEx.toBundle(data.getMap(BRIDGE_MSG_DATA)));
                                    break;
                                case Boolean:
                                    bundle.putBoolean("rsp", data.getBoolean(BRIDGE_MSG_DATA));
                                    break;
                                case Number:
                                    // can be int or double
                                    bundle.putDouble("rsp", data.getDouble(BRIDGE_MSG_DATA));
                                    break;
                                case String:
                                    bundle.putString("rsp", data.getString(BRIDGE_MSG_DATA));
                                    break;
                                case Null:
                                    break;
                            }
                        }
                    }
                });
            }
        }, new Callback() {
            @Override
            public void invoke(final Object... args) {
                mReactContextWrapper.runOnUiQueueThread(new Runnable() {
                    @Override
                    public void run() {
                        WritableMap writableMap = (WritableMap) args[0];
                        completionListener.onError(
                                writableMap.getString("code"),
                                writableMap.getString("message"));
                    }
                });
            }
        });

        pendingPromiseByRequestId.put(id, promise);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                Promise promise = pendingPromiseByRequestId.remove(id);
                if (promise != null) {
                    promise.reject("EREQUESTTIMEOUT", "Request timeout");
                }
            }
        }, request.getTimeoutMs());

        Log.d(TAG, String.format("Sending request[name:%s id:%s]", request.getName(), id));

        if (request.getDispatchMode().equals(ElectrodeBridgeRequest.DispatchMode.JS)) {
            WritableMap message = buildMessage(
                    id, request.getName(), Arguments.fromBundle(request.getData()));

            mReactContextWrapper.emitEvent(BRIDE_REQUEST, message);
        } else if (request.getDispatchMode().equals(ElectrodeBridgeRequest.DispatchMode.NATIVE)) {
            dispatchRequest(request.getName(), id, Arguments.fromBundle(request.getData()), promise);
        }
    }

    /**
     * Dispatch a request on the native side
     *
     * @param name    The name of the request
     * @param id      The request id
     * @param data    The request data
     * @param promise A promise to reject or resolve the request asynchronously
     */
    @ReactMethod
    @SuppressWarnings("unused")
    public void dispatchRequest(String name, String id, ReadableMap data, Promise promise) {
        Log.d(TAG, String.format("dispatchRequest[name:%s id:%s]", name, id));
        mRequestDispatcher.dispatchRequest(name, id, data, promise);
    }

    /**
     * Dispatch an event on the native side
     *
     * @param name The name of the event
     * @param id   The id of the event
     * @param data The event data
     */
    @ReactMethod
    @SuppressWarnings("unused")
    public void dispatchEvent(final String name, final String id, final ReadableMap data) {
        Log.d(TAG, String.format("onEvent[name:%s id:%s]", name, id));

        // This event represents a bridge response
        if (name.equals(BRIDGE_RESPONSE)) {
            // Get id of associated request
            String parentRequestId = data.getString(BRIDGE_REQUEST_ID);
            Log.d(TAG, String.format("Received response [id:%s]", parentRequestId));
            // Get the pending promise of the associated request
            Promise promise = pendingPromiseByRequestId.remove(parentRequestId);
            // If this is an error response
            // Reject the pending promise with error code and message
            if (data.hasKey(BRIDGE_RESPONSE_ERROR)) {
                String errorMessage = data
                        .getMap(BRIDGE_RESPONSE_ERROR)
                        .getString(BRIDGE_RESPONSE_ERROR_MESSAGE);

                String errorCode = UNKNOWN_ERROR_CODE;
                if (data.getMap(BRIDGE_RESPONSE_ERROR)
                        .hasKey(BRIDGE_RESPONSE_ERROR_CODE)) {
                    errorCode = data
                            .getMap(BRIDGE_RESPONSE_ERROR)
                            .getString(BRIDGE_RESPONSE_ERROR_CODE);
                }
                promise.reject(errorCode, errorMessage);
            }
            // If this is a success response with a payload
            // Resolve the promise with the data
            else if (data.hasKey(BRIDGE_MSG_DATA)) {
                promise.resolve(data);
            }
            // If this is a success response without a payload
            // Resolve the promise with null
            else if (!data.hasKey(BRIDGE_MSG_DATA)) {
                promise.resolve(null);
            }
            // Unknown type of response :S
            else {
                promise.reject(new UnsupportedOperationException());
            }
        } else {
            mReactContextWrapper.runOnUiQueueThread(new Runnable() {
                @Override
                public void run() {
                    mEventDispatcher.dispatchEvent(id, name, data);
                }
            });
        }
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private WritableMap buildMessage(String id, String name, WritableMap data) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(BRIDGE_MSG_ID, id);
        writableMap.putString(BRIDGE_MSG_NAME, name);
        writableMap.putMap(BRIDGE_MSG_DATA, data);

        return writableMap;
    }

    public interface ReactNativeReadyListener {
        void onReactNativeReady();
    }

    private static ReactNativeReadyListener sReactNativeReadyListener;

    public static void registerReactNativeReadyListener(ReactNativeReadyListener listener) {
        // If react native initialization is already completed, just call listener
        // immediately
        if (sIsReactNativeReady) {
            listener.onReactNativeReady();
        }
        // Else it will get invoked whenever react native initialization is done
        else {
            sReactNativeReadyListener = listener;
        }
    }

    public void onReactNativeInitialized() {
        sIsReactNativeReady = true;
        if (sReactNativeReadyListener != null) {
            sReactNativeReadyListener.onReactNativeReady();
        }
    }
}
