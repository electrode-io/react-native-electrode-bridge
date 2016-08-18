package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Handler;
import android.os.Looper;
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
    private static final String BRDIGE_RESPONSE_ERROR_CODE = "code";
    private static final String BRIDGE_RESPONSE_ERROR_MESSAGE = "message";
    private static final String BRIDGE_MSG_DATA = "data";
    private static final String BRIDGE_MSG_TYPE = "type";
    private static final String BRIDGE_MSG_ID = "id";
    private static final String BRIDGE_REQUEST_ID = "requestId";
    private static final String UNKNOWN_ERROR_CODE = "EUNKNOWN";

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
         * @param data The data of the event as a ReadableMap
         */
        void dispatchEvent(@NonNull String id,
                           @NonNull String type,
                           @NonNull ReadableMap data);
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
         * @param data The data of the request as a ReadableMap
         * @param promise A promise to fulfil upon request completion
         */
        void dispatchRequest(@NonNull String type,
                             @NonNull String id,
                             @NonNull ReadableMap data,
                             @NonNull Promise promise);
    }

    /**
     * Dispatch an event on the native side
     *
     * @param type The type of the event
     * @param id The id of the event
     * @param data The event data
     */
    @ReactMethod
    @SuppressWarnings("unused")
    public void dispatchEvent(String type, String id, ReadableMap data) {
        Log.d(TAG, String.format("onEvent[type:%s id:%s]", type, id));

        if (type.equals(BRIDGE_RESPONSE)) {
            String parentRequestId = data.getString(BRIDGE_REQUEST_ID);
            Log.d(TAG, String.format("Received response [id:%s]", parentRequestId));
            Promise promise = pendingPromiseByRequestId.remove(parentRequestId);
            if (data.hasKey(BRIDGE_RESPONSE_ERROR)) {
                String errorMessage = data
                        .getMap(BRIDGE_RESPONSE_ERROR)
                        .getString(BRIDGE_RESPONSE_ERROR_MESSAGE);

                String errorCode = UNKNOWN_ERROR_CODE;
                if (data
                        .getMap(BRIDGE_RESPONSE_ERROR)
                        .hasKey(BRDIGE_RESPONSE_ERROR_CODE)) {
                    errorCode = data
                            .getMap(BRIDGE_RESPONSE_ERROR)
                            .getString(BRDIGE_RESPONSE_ERROR_CODE);
                }
                promise.reject(errorCode, errorMessage);
            } else if (data.hasKey(BRIDGE_MSG_DATA)) {
                promise.resolve(data.getMap(BRIDGE_MSG_DATA));
            } else {
                promise.reject(new UnsupportedOperationException());
            }
        } else {
            mEventDispatcher.dispatchEvent(id, type, data);
        }
    }

    /**
     * Dispatch a request on the native side
     *
     * @param type The type of the request
     * @param id The request id
     * @param data The request data
     * @param promise A promise to reject or resolve the request asynchronously
     */
    @ReactMethod
    @SuppressWarnings("unused")
    public void dispatchRequest(String type, String id, ReadableMap data, Promise promise) {
        Log.d(TAG, String.format("dispatchRequest[type:%s id:%s]", type, id));
        mRequestDispatcher.dispatchRequest(type, id, data, promise);
    }

    /**
     * Emits an event with some data to the JS react native side
     *
     * @param event The event to emit
     */
    @SuppressWarnings("unused")
    public void emitEvent(@NonNull ElectrodeBridgeEvent event) {
        String id = getUUID();
        WritableMap message = buildMessage(id, event.getType(), Arguments.fromBundle(event.getData()));

        Log.d(TAG, String.format("Emitting event[type:%s id:%s]", event.getType(), id));

        if (event.getDispatchMode() == ElectrodeBridgeEvent.DispatchMode.JS) {
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(BRIDGE_EVENT, message);
        } else if (event.getDispatchMode() == ElectrodeBridgeEvent.DispatchMode.NATIVE) {
            dispatchEvent(event.getType(), id, Arguments.fromBundle(event.getData()));
        } else if (event.getDispatchMode() == ElectrodeBridgeEvent.DispatchMode.GLOBAL) {
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(BRIDGE_EVENT, message);
            dispatchEvent(event.getType(), id, Arguments.fromBundle(event.getData()));
        }
    }

    /**
     * Sends a request
     *
     * @param request The request to send
     * @param completionListener Listener to be called upon request completion
    */
    @SuppressWarnings("unused")
    public void sendRequest(
            @NonNull ElectrodeBridgeRequest request,
            @NonNull final RequestCompletionListener completionListener) {
        final String id = getUUID();

        final Promise promise = new PromiseImpl(new Callback() {
            @Override
            public void invoke(Object... args) {
                completionListener.onSuccess(Arguments.toBundle((ReadableMap)args[0]));
            }
        }, new Callback() {
            @Override
            public void invoke(Object... args) {
                WritableMap writableMap = (WritableMap)args[0];
                completionListener.onError(
                        writableMap.getString("code"),
                        writableMap.getString("message"));
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

        Log.d(TAG, String.format("Sending request[type:%s id:%s]", request.getType(), id));

        if (request.getDispatchMode().equals(ElectrodeBridgeRequest.DispatchMode.JS)) {
            WritableMap message = buildMessage(
                    id, request.getType(), Arguments.fromBundle(request.getData()));

            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(BRIDE_REQUEST, message);
        } else if (request.getDispatchMode().equals(ElectrodeBridgeRequest.DispatchMode.NATIVE)) {
            dispatchRequest(request.getType(), id, Arguments.fromBundle(request.getData()), promise);
        }
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

    private WritableMap buildMessage(String id, String type, WritableMap data) {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(BRIDGE_MSG_ID, id);
        writableMap.putString(BRIDGE_MSG_TYPE, type);
        writableMap.putMap(BRIDGE_MSG_DATA, data);

        return writableMap;
    }

}
