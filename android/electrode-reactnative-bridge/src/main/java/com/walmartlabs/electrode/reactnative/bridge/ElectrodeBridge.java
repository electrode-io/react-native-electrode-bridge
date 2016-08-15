package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.PromiseImpl;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ElectrodeBridge extends ReactContextBaseJavaModule {

    private static final String TAG = ElectrodeBridge.class.getSimpleName();

    private static final String BRIDGE_EVENT = "electrode.bridge.event";
    private static final String BRIDE_REQUEST = "electrode.bridge.request";
    private static final String BRIDGE_RESPONSE = "electrode.bridge.response";
    private static final String BRIDGE_RESPONSE_ERROR = "error";
    private static final String BRDIGE_RESPONSE_ERROR_NAME = "name";
    private static final String BRIDGE_RESPONSE_ERROR_MESSAGE = "message";
    private static final String BRIDGE_MSG_DATA = "data";
    private static final String BRIDGE_MSG_TYPE = "type";
    private static final String BRIDGE_MSG_ID = "id";

    private final ReactApplicationContext mReactContext;
    private final EventDispatcher mEventDispatcher;
    private final RequestDispatcher mRequestDispatcher;

    private final ConcurrentHashMap<String, Promise> pendingPromiseByRequestId = new ConcurrentHashMap<>();
    private final EventRegistrar<EventDispatcherImpl.EventListener> mEventRegistrar = new EventRegistrarImpl<>();
    private final RequestRegistrar<RequestDispatcherImpl.RequestHandler> mRequestRegistrar = new RequestRegistrarImpl<>();

    /**
     * Initializes a new instance of ElectrodeBridge
     * @param reactContext The react application context
     */
    public ElectrodeBridge(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mEventDispatcher = new EventDispatcherImpl(mEventRegistrar);
        mRequestDispatcher = new RequestDispatcherImpl(mRequestRegistrar);
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
         * @param id The event id
         * @param type The type of the event to dispatch
         * @param payload The payload of the event as a ReadableMap
         */
        void dispatchEvent(@NonNull String id,
                           @NonNull String type,
                           @NonNull ReadableMap payload);
    }

    /**
     * Provides a method to dispatch a request
     */
    public interface RequestDispatcher {
        /**
         * Dispatch a request
         *
         * @param type The type of the request to dispatch
         * @param id The request id
         * @param payload The payload of the request as a ReadableMap
         * @param promise A promise to fulfil upon request completion
         */
        void dispatchRequest(@NonNull String type,
                             @NonNull String id,
                             @NonNull ReadableMap payload,
                             @NonNull Promise promise);
    }

    /**
     * Provides methods to be informed of a request completion state
     */
    public interface RequestCompletionListener {
        /**
         * Called if request was successful
         * @param payload The response payload as a bundle (empty bundle if no data)
         */
        void onSuccess(@NonNull Bundle payload);

        /**
         * Called if request failed
         * @param code The error code ("EUNKNOWN" if no error code was set)
         * @param message The error message
         */
        void onError(@NonNull  String code, @NonNull String message);
    }

    /**
     * Dispatch an event on the native side
     *
     * @param type The type of the event
     * @param id The id of the event
     * @param payload The event payload
     */
    @ReactMethod
    @SuppressWarnings("unused")
    public void dispatchEvent(String type, String id, ReadableMap payload) {
        Log.d(TAG, String.format("onEvent[type:%s id:%s]", type, id));

        if (type.equals(BRIDGE_RESPONSE)) {
            String parentRequestId = payload.getString(BRIDGE_MSG_ID);
            Log.d(TAG, String.format("Received response [id:%s]", parentRequestId));
            Promise promise = pendingPromiseByRequestId.remove(parentRequestId);
            if (payload.hasKey(BRIDGE_RESPONSE_ERROR)) {
                String errorMessage = payload
                        .getMap(BRIDGE_RESPONSE_ERROR)
                        .getString(BRIDGE_RESPONSE_ERROR_MESSAGE);

                String errorName = "EUNKNOWN";
                if (payload
                        .getMap(BRIDGE_RESPONSE_ERROR)
                        .hasKey(BRDIGE_RESPONSE_ERROR_NAME)) {
                    errorName = payload
                            .getMap(BRIDGE_RESPONSE_ERROR)
                            .getString(BRDIGE_RESPONSE_ERROR_NAME);
                }
                promise.reject(errorName, errorMessage);
            } else if (payload.hasKey(BRIDGE_MSG_DATA)) {
                promise.resolve(payload.getMap(BRIDGE_MSG_DATA));
            } else {
                promise.reject(new UnsupportedOperationException());
            }
        } else {
            mEventDispatcher.dispatchEvent(id, type, payload);
        }
    }

    /**
     * Dispatch a request on the native side
     *
     * @param type The type of the request
     * @param id The request id
     * @param payload The request payload
     * @param promise A promise to reject or resolve the request asynchronously
     */
    @ReactMethod
    @SuppressWarnings("unused")
    public void dispatchRequest(String type, String id, ReadableMap payload, Promise promise) {
        Log.d(TAG, String.format("dispatchRequest[type:%s id:%s]", type, id));
        mRequestDispatcher.dispatchRequest(type, id, payload, promise);
    }

    /**
     * Emits an event without any payload to the JS react native side
     * @param type The type of the event
     */
    public void emitEventToJs(@NonNull String type) {
        emitEventToJs(type, Bundle.EMPTY);
    }

    /**
     * Emits an event with some payload to the JS react native side
     *
     * @param type The type of the event
     * @param payload The event payload
     */
    @SuppressWarnings("unused")
    public void emitEventToJs(@NonNull String type, @NonNull Bundle payload) {
        String id = getUUID();
        WritableMap message = buildMessage(id, type, Arguments.fromBundle(payload));

        Log.d(TAG, String.format("Emitting event[type:%s id:%s]", type, id));

        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(BRIDGE_EVENT, message);
    }

    /**
     * Sends a request without any payload the JS react native side
     *
     * @param type The type of the request
     * @param requestCompletionListener A RequestCompletionListener to be informed of request
     *  completion success or failure
     */
    public void sendRequestToJs(String type, RequestCompletionListener requestCompletionListener) {
        sendRequestToJs(type, Bundle.EMPTY, requestCompletionListener);
    }

    /**
     * Sends a request with some payload to the JS react native side
     *
     * @param type The type of the request
     * @param payload The request payload
     * @param requestCompletionListener A RequestCompletionListener to be informed of request
     *  completion success or failure
    */
    @SuppressWarnings("unused")
    public void sendRequestToJs(String type, Bundle payload, final RequestCompletionListener requestCompletionListener) {
        String id = getUUID();
        WritableMap message = buildMessage(id, type, Arguments.fromBundle(payload));

        Promise promise = new PromiseImpl(new Callback() {
            @Override
            public void invoke(Object... args) {
                requestCompletionListener.onSuccess(Arguments.toBundle((ReadableMap)args[0]));
            }
        }, new Callback() {
            @Override
            public void invoke(Object... args) {
                WritableMap writableMap = (WritableMap)args[0];
                requestCompletionListener.onError(
                        writableMap.getString("code"),
                        writableMap.getString("message"));
            }
        });

        pendingPromiseByRequestId.put(id, promise);

        Log.d(TAG, String.format("Sending request[type:%s id:%s]", type, id));

        mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(BRIDE_REQUEST, message);
    }

    /**
     * @return The request handler registrar
     */
    public RequestRegistrar<RequestDispatcherImpl.RequestHandler> requestRegistrar() {
        return mRequestRegistrar;
    }

    /**
     * @return The event listener register
     */
    public EventRegistrar<EventDispatcherImpl.EventListener> eventRegistrar() {
        return mEventRegistrar;
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private WritableMap buildMessage(String id, String type, WritableMap payload) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(BRIDGE_MSG_ID, id);
        writableMap.putString(BRIDGE_MSG_TYPE, type);
        writableMap.putMap(BRIDGE_MSG_DATA, payload);

        return writableMap;
    }

}
