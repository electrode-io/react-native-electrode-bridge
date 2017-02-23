package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BridgeFailureMessage implements FailureMessage {

    private final String code;
    private final String message;
    private final Exception exception;
    private final String debugMessage;

    private BridgeFailureMessage(@NonNull String code, @NonNull String message, @Nullable Exception exception, @Nullable String debugMessage) {
        this.code = code;
        this.message = message;
        this.exception = exception;

        if (debugMessage == null) {
            this.debugMessage = exception != null ? exception.getMessage() : null;
        } else {
            this.debugMessage = debugMessage;
        }
    }

    public static BridgeFailureMessage create(@NonNull String code, @NonNull String message) {
        return new BridgeFailureMessage(code, message, null, null);
    }

    public static BridgeFailureMessage create(@NonNull String code, @NonNull String message, @Nullable Exception exception) {
        return new BridgeFailureMessage(code, message, exception, null);
    }

    public static BridgeFailureMessage create(@NonNull String code, @NonNull String message, @Nullable String debugMessage) {
        return new BridgeFailureMessage(code, message, null, debugMessage);
    }

    @NonNull
    @Override
    public String getCode() {
        return code;
    }

    @NonNull
    @Override
    public String getMessage() {
        return message;
    }


    @Nullable
    @Override
    public Throwable getException() {
        return exception;
    }

    @Nullable
    @Override
    public String getDebugMessage() {
        return debugMessage;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "-> code:" + code
                + ", message:" + message
                + ", exeception:" + exception
                + ", debugMessage:" + debugMessage;
    }
}
