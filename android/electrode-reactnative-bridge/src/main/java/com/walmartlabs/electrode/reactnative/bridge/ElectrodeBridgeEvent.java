package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

public class ElectrodeBridgeEvent extends BridgeMessage {

    private static final String TAG = ElectrodeBridgeEvent.class.getSimpleName();

    @Nullable
    public static ElectrodeBridgeEvent create(@NonNull ReadableMap messageMap) {
        ElectrodeBridgeEvent bridgeMessage = null;
        if (isValid(messageMap, BridgeMessage.Type.EVENT)) {
            bridgeMessage = new ElectrodeBridgeEvent(messageMap);
        } else {
            Logger.w(TAG, "Unable to createMessage a bridge message, invalid data received(%s)", messageMap);
        }
        return bridgeMessage;
    }

    private ElectrodeBridgeEvent(ReadableMap messageMap) {
        super(messageMap);
    }

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        super(eventBuilder.mName, getUUID(), BridgeMessage.Type.EVENT, eventBuilder.mData);
    }

    public static class Builder {
        private final String mName;
        private Object mData;

        /**
         * Initializes a new event builder
         *
         * @param name The name of the event to build
         */
        public Builder(String name) {
            this.mName = name;
            this.mData = Bundle.EMPTY;
        }

        /**
         * Specifies the event data
         *
         * @param data The data
         * @return Current builder instance for chaining
         */
        public Builder withData(Object data) {
            this.mData = data;
            return this;
        }


        /**
         * Builds the event
         *
         * @return The built event
         */
        public ElectrodeBridgeEvent build() {
            return new ElectrodeBridgeEvent(this);
        }
    }
}
