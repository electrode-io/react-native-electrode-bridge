package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public class ElectrodeBridgeResponse extends BridgeMessage {

    private static final String TAG = ElectrodeBridgeResponse.class.getSimpleName();

    private static final String BRIDGE_MSG_ERROR = "error";
    private static final String BRIDGE_RESPONSE_ERROR_CODE = "code";
    private static final String BRIDGE_RESPONSE_ERROR_MESSAGE = "message";
    private static final String UNKNOWN_ERROR_CODE = "EUNKNOWN";

    /**
     * Constructs a com.walmartlabs.electrode.reactnative.bridge.BridgeMessage if the given MAP has all the required data to construct a message.
     *
     * @param messageMap {@link ReadableMap} map sent by React native
     * @return com.walmartlabs.electrode.reactnative.bridge.BridgeMessage
     */
    @Nullable
    public static ElectrodeBridgeResponse create(@NonNull ReadableMap messageMap) {
        ElectrodeBridgeResponse bridgeResponse = null;
        if (isValid(messageMap, BridgeMessage.Type.RESPONSE)) {
            String eventName = messageMap.getString(BRIDGE_MSG_NAME);
            String eventId = messageMap.getString(BRIDGE_MSG_ID);
            Type type = BridgeMessage.Type.getType(messageMap.getString(BRIDGE_MSG_TYPE));

            Bundle data = null;
            if (messageMap.hasKey(BRIDGE_MSG_DATA)) {
                data = ArgumentsEx.toBundle(messageMap, BRIDGE_MSG_DATA);
            }

            FailureMessage failureMessage = null;
            if (messageMap.hasKey(BRIDGE_MSG_ERROR)) {
                Bundle error = ArgumentsEx.toBundle(messageMap.getMap(BRIDGE_MSG_ERROR));
                if (!error.isEmpty()) {
                    String code = error.getString(BRIDGE_RESPONSE_ERROR_CODE);
                    String message = error.getString(BRIDGE_RESPONSE_ERROR_MESSAGE);
                    failureMessage = BridgeFailureMessage.create(code != null ? code : UNKNOWN_ERROR_CODE, message != null ? message : "Unknown error");
                }
            }

            if (type != null) {
                bridgeResponse = new ElectrodeBridgeResponse(eventName, eventId, type, data, failureMessage);
            } else {
                Logger.w(TAG, "Message type(%s) not supported, cannot construct com.walmartlabs.electrode.reactnative.bridge.BridgeMessage", messageMap.getString(BRIDGE_MSG_TYPE));
            }

        } else {
            Logger.w(TAG, "Unable to createMessage a bridge message, invalid data received(%s)", messageMap);
        }
        return bridgeResponse;
    }

    @Nullable
    public static ElectrodeBridgeResponse createResponseForRequest(@NonNull ElectrodeBridgeRequest request, @Nullable Object responseData, @Nullable FailureMessage failureMessage) {
        return new ElectrodeBridgeResponse(request.getName(), request.getId(), BridgeMessage.Type.RESPONSE, responseData, failureMessage);
    }

    private final FailureMessage failureMessage;

    private ElectrodeBridgeResponse(@NonNull String name, @NonNull String id, @NonNull Type type, @Nullable Object data, @Nullable FailureMessage failureMessage) {
        super(name, id, type, data);
        this.failureMessage = failureMessage;
    }

    @Nullable
    public FailureMessage getFailureMessage() {
        return failureMessage;
    }

    @NonNull
    public WritableMap map() {
        WritableMap writableMap = super.map();
        if (failureMessage != null) {
            WritableMap error = Arguments.createMap();
            error.putString(BRIDGE_RESPONSE_ERROR_CODE, failureMessage.getCode());
            error.putString(BRIDGE_RESPONSE_ERROR_MESSAGE, failureMessage.getMessage());
            writableMap.putMap(BRIDGE_MSG_ERROR, error);
        }
        return writableMap;
    }
}
