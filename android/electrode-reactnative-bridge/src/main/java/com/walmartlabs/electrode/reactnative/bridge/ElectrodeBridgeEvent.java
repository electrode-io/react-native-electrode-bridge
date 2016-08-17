package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;

public class ElectrodeBridgeEvent {
    private final String mType;
    private final Bundle mData;

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        mType = eventBuilder.mType;
        mData = eventBuilder.mData;
    }

    /**
     * @return The type of this event
     */
    public String getType() {
        return mType;
    }

    /**
     * @return The data of this event
     */
    public Bundle getData() {
        return mData;
    }

    public static class Builder {
        private final String mType;
        private Bundle mData;

        /**
         * Initializes a new event builder
         *
         * @param type The type of the event to build
         */
        public Builder(String type) {
            this.mType = type;
            this.mData = Bundle.EMPTY;
        }

        /**
         * Specifies the event data
         *
         * @param data The data
         * @return Current builder instance for chaining
         */
        public Builder withData(Bundle data) {
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
