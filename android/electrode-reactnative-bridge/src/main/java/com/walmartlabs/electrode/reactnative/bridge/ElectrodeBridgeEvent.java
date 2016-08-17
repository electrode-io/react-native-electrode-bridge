package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;

public class ElectrodeBridgeEvent {
    private final String mType;
    private final Bundle mPayload;

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        mType = eventBuilder.mType;
        mPayload = eventBuilder.mPayload;
    }

    /**
     * @return The type of this event
     */
    public String getType() {
        return mType;
    }

    /**
     * @return The payload of this event
     */
    public Bundle getPayload() {
        return mPayload;
    }

    public static class Builder {
        private final String mType;
        private Bundle mPayload;

        /**
         * Initializes a new event builder
         *
         * @param type The type of the event to build
         */
        public Builder(String type) {
            this.mType = type;
            this.mPayload = Bundle.EMPTY;
        }

        /**
         * Specifies the event payload
         *
         * @param payload The payload
         * @return Current builder instance for chaining
         */
        public Builder withPayload(Bundle payload) {
            this.mPayload = payload;
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
