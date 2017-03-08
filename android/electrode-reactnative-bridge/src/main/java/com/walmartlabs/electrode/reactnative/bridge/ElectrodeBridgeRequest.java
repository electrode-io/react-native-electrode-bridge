package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

public class ElectrodeBridgeRequest extends BridgeMessage {
    private static final String TAG = ElectrodeBridgeRequest.class.getSimpleName();
    private static final int DEFAULT_REQUEST_TIMEOUT_MS = 5000;

    private final int mTimeoutMs;
    private boolean isJsInitiated;

    public enum DispatchMode {
        JS, NATIVE, GLOBAL
    }

    @Nullable
    public static ElectrodeBridgeRequest create(@NonNull ReadableMap messageMap) {
        ElectrodeBridgeRequest bridgeMessage = null;
        if (isValid(messageMap, BridgeArguments.Type.REQUEST)) {
            String eventName = messageMap.getString(BRIDGE_MSG_NAME);
            String eventId = messageMap.getString(BRIDGE_MSG_ID);
            Bundle data = null;
            if (messageMap.hasKey(BRIDGE_MSG_DATA)) {
                String requestKey = BridgeArguments.Type.REQUEST.getKey();
                if (messageMap.getMap(BRIDGE_MSG_DATA).hasKey(requestKey)) {
                    data = ArgumentsEx.toBundle(messageMap.getMap(BRIDGE_MSG_DATA));
                } else {
                    Logger.w(TAG, "Looks like the request data from JS is not having an %s key entry, the data will be ignored.", requestKey);
                }
            }
            bridgeMessage = new ElectrodeBridgeRequest.Builder(eventName).withData(data).id(eventId).build();
            bridgeMessage.isJsInitiated = true;
        } else {
            Logger.w(TAG, "Unable to createMessage a bridge message, invalid data received(%s)", messageMap);
        }
        return bridgeMessage;
    }

    private ElectrodeBridgeRequest(Builder requestBuilder) {
        super(requestBuilder.mName, requestBuilder.mId == null ? getUUID() : requestBuilder.mId, BridgeArguments.Type.REQUEST, requestBuilder.mData);
        mTimeoutMs = requestBuilder.mTimeoutMs;

    }

    /**
     * @return The timeout of this request
     */
    public int getTimeoutMs() {
        return this.mTimeoutMs;
    }

    /**
     * Indicates if a request was initiated by JS.
     *
     * @return true | false
     */
    public boolean isJsInitiated() {
        return isJsInitiated;
    }

    public static class Builder {
        private final String mName;
        private Bundle mData;
        private int mTimeoutMs;
        private String mId;
        private DispatchMode mDispatchMode;

        /**
         * Initializes a new request builder
         *
         * @param name The name of the request to build
         */
        public Builder(String name) {
            mName = name;
            mTimeoutMs = DEFAULT_REQUEST_TIMEOUT_MS;
            mData = Bundle.EMPTY;
            mDispatchMode = DispatchMode.JS;
        }

        /**
         * Specifies the request timeout
         *
         * @param timeoutMs The timeout in milliseconds
         * @return Current builder instance for chaining
         */
        public Builder withTimeout(int timeoutMs) {
            this.mTimeoutMs = timeoutMs;
            return this;
        }

        /**
         * Specifies the request data
         *
         * @param data The data
         * @return Current builder instance for chaining
         */
        public Builder withData(Bundle data) {
            if (data != null
                    && !data.isEmpty()
                    && !data.containsKey(BridgeArguments.Type.REQUEST.getKey())) {
                throw new IllegalArgumentException("The request data should be put inside " + BridgeArguments.Type.REQUEST.getKey() + " key");
            }
            this.mData = data;
            return this;
        }

        public Builder id(String id) {
            mId = id;
            return this;
        }

        /**
         * Specifies the dispatch mode
         *
         * @param dispatchMode The dispatch mode to use
         * @return Current builder instance for chaining
         */
        public Builder withDispatchMode(DispatchMode dispatchMode) {
            this.mDispatchMode = dispatchMode;
            return this;
        }

        /**
         * Builds the request
         *
         * @return The built request
         */
        public ElectrodeBridgeRequest build() {
            return new ElectrodeBridgeRequest(this);
        }
    }
}
