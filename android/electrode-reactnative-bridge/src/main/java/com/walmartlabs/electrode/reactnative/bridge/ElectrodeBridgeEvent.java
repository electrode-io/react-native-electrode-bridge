package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;

public class ElectrodeBridgeEvent {
    private final String mType;
    private final Bundle mData;
    private final DispatchMode mDispatchMode;

    public enum DispatchMode {
        JS, NATIVE, GLOBAL
    }

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        mType = eventBuilder.mType;
        mData = eventBuilder.mData;
        mDispatchMode = eventBuilder.mDispatchMode;
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

    /**
     * @return The dispatch mode of this event
     */
    public DispatchMode getDispatchMode() {
        return mDispatchMode;
    }

    public static class Builder {
        private final String mType;
        private Bundle mData;
        private DispatchMode mDispatchMode;

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
         * Builds the event
         *
         * @return The built event
         */
        public ElectrodeBridgeEvent build() {
            return new ElectrodeBridgeEvent(this);
        }
    }
}
