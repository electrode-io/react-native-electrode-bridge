package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

public interface EventDispatcher {
    void dispatchEvent(@NonNull ElectrodeBridgeEvent event);
}
