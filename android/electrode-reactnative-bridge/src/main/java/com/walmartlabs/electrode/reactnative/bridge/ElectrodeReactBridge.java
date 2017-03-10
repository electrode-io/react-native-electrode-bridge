package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReadableMap;

public interface ElectrodeReactBridge {

    /**
     * Invoked by React side to communicate to bridge.
     *
     * @param bridgeMessage the {@link ReadableMap} representation of a {{@link BridgeMessage}}
     */
    void sendMessage(@NonNull ReadableMap bridgeMessage);
}
