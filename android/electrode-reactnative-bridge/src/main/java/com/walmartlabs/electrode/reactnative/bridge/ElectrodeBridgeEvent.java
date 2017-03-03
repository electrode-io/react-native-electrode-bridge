package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;

public class ElectrodeBridgeEvent {
    private final String mName;
    private final Bundle mData;

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        mName = eventBuilder.mName;
        mData = eventBuilder.mData;
    }

    /**
     * @return The name of this event
     */
    public String getName() {
        return mName;
    }

    /**
     * @return The data of this event
     */
    public Bundle getData() {
        return mData;
    }

    public static class Builder {
        private final String mName;
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
