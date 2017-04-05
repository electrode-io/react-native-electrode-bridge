package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.Nullable;

import java.util.Map;

public interface ReactConstantsProvider {
    /**
     * Returns the constant values exposed to JavaScript.
     * <p>
     * Its implementation is not required but is very useful to key pre-defined values that need to be communicated from JavaScript to Java in sync
     *
     * @return Map
     */
    @Nullable
    Map<String, Object> getConstants();
}
