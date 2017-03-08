package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

public class ElectrodeBridgeEvent extends BridgeMessage {

    private static final String TAG = ElectrodeBridgeEvent.class.getSimpleName();

    @Nullable
    public static ElectrodeBridgeEvent create(@NonNull ReadableMap messageMap) {
        ElectrodeBridgeEvent bridgeMessage = null;
        if (isValid(messageMap, BridgeArguments.Type.EVENT)) {
            String eventName = messageMap.getString(BRIDGE_MSG_NAME);
            String eventId = messageMap.getString(BRIDGE_MSG_ID);
            Bundle data = null;
            if (messageMap.hasKey(BRIDGE_MSG_DATA)) {
                String eventKey = BridgeArguments.Type.EVENT.getKey();
                if (messageMap.getMap(BRIDGE_MSG_DATA).hasKey(eventKey)) {
                    data = ArgumentsEx.toBundle(messageMap.getMap(BRIDGE_MSG_DATA));
                } else {
                    Logger.w(TAG, "Looks like the event data from JS is not having an '%s' key entry, the data will be ignored.", eventKey);
                }

            }
            bridgeMessage = new Builder(eventName).withData(data).id(eventId).build();

        } else {
            Logger.w(TAG, "Unable to createMessage a bridge message, invalid data received(%s)", messageMap);
        }
        return bridgeMessage;
    }

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        super(eventBuilder.mName, eventBuilder.mId == null ? getUUID() : eventBuilder.mId, BridgeArguments.Type.EVENT, eventBuilder.mData);
    }

    public static class Builder {
        private final String mName;
        private String mId;
        private Bundle mData;

        /**
         * Initializes a new event builder
         *
         * @param name The name of the event to build
         */
        public Builder(String name) {
            this.mName = name;
            this.mData = Bundle.EMPTY;
        }

        public Builder id(String id) {
            mId = id;
            return this;
        }

        /**
         * Specifies the event data
         *
         * @param data The data
         * @return Current builder instance for chaining
         */
        public Builder withData(Bundle data) {
            if (data != null
                    && !data.isEmpty()
                    && !data.containsKey(BridgeArguments.Type.EVENT.getKey())) {
                throw new IllegalArgumentException("The event data should be put inside " + BridgeArguments.Type.EVENT.getKey() + " key");
            }
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
