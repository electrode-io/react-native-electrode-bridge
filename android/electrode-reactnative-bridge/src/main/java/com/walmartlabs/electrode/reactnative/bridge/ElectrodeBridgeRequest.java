package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;

public class ElectrodeBridgeRequest {
    private static final int DEFAULT_REQUEST_TIMEOUT = 5000;

    private final String mType;
    private final Bundle mPayload;
    private final int mTimeoutMs;
    private final RequestCompletionListener mCompletionListener;

    private ElectrodeBridgeRequest(Builder requestBuilder) {
        mType = requestBuilder.mType;
        mPayload = requestBuilder.mPayload;
        mTimeoutMs = requestBuilder.mTimeoutMs;
        mCompletionListener = requestBuilder.mCompletionListener;
    }

    public String getType() {
        return this.mType;
    }

    public RequestCompletionListener getRequestCompletionListener() {
        return this.mCompletionListener;
    }

    public Bundle getPayload() {
        return this.mPayload;
    }

    public int getTimeoutMs() {
        return this.mTimeoutMs;
    }

    public static class Builder {
        private final String mType;
        private final RequestCompletionListener mCompletionListener;
        private Bundle mPayload;
        private int mTimeoutMs;

        public Builder(String type, RequestCompletionListener completionListener) {
            mType = type;
            mCompletionListener = completionListener;
            mTimeoutMs = DEFAULT_REQUEST_TIMEOUT;
            mPayload = Bundle.EMPTY;
        }

        public Builder withTimeout(int timeoutMs) {
            this.mTimeoutMs = timeoutMs;
            return this;
        }

        public Builder withPayload(Bundle payload) {
            this.mPayload = payload;
            return this;
        }

        public ElectrodeBridgeRequest build() {
            return new ElectrodeBridgeRequest(this);
        }
     }
}
