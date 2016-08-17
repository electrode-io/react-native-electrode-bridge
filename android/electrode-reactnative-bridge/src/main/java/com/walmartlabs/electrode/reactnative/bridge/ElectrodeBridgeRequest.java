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

    /**
     * @return The type of this request
     */
    public String getType() {
        return this.mType;
    }

    /**
     * @return The completion listener instance associated to this request
     */
    public RequestCompletionListener getRequestCompletionListener() {
        return this.mCompletionListener;
    }

    /**
     * @return The payload of this request
     */
    public Bundle getPayload() {
        return this.mPayload;
    }

    /**
     * @return The timeout of this request
     */
    public int getTimeoutMs() {
        return this.mTimeoutMs;
    }

    public static class Builder {
        private final String mType;
        private final RequestCompletionListener mCompletionListener;
        private Bundle mPayload;
        private int mTimeoutMs;

        /**
         * Initializes a new request builder
         *
         * @param type The type of the request to build
         * @param completionListener The completion listener to associate to the request to build
         */
        public Builder(String type, RequestCompletionListener completionListener) {
            mType = type;
            mCompletionListener = completionListener;
            mTimeoutMs = DEFAULT_REQUEST_TIMEOUT;
            mPayload = Bundle.EMPTY;
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
         * Specifies the request payload
         *
         * @param payload The payload
         * @return Current builder instance for chaining
         */
        public Builder withPayload(Bundle payload) {
            this.mPayload = payload;
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
