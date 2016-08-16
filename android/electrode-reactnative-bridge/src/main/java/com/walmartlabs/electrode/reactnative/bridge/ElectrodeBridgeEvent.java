package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;

public class ElectrodeBridgeEvent {
    private final String mType;
    private final Bundle mPayload;

    private ElectrodeBridgeEvent(Builder eventBuilder) {
        mType = eventBuilder.mType;
        mPayload = eventBuilder.mPayload;
    }

    public String getType() {
        return mType;
    }

    public Bundle getPayload() {
        return mPayload;
    }

    public static class Builder {
        private final String mType;
        private Bundle mPayload;

        public Builder(String type) {
            this.mType = type;
            this.mPayload = Bundle.EMPTY;
        }

        public Builder withPayload(Bundle payload) {
            this.mPayload = payload;
            return this;
        }

        public ElectrodeBridgeEvent build() {
            return new ElectrodeBridgeEvent(this);
        }
    }
}
