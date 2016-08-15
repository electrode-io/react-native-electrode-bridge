package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;

public class ElectrodeBridgeHolder {

    private static ElectrodeBridge sElectrodeBridge = null;
    private static ArrayList<OnBridgeReadyListener> onBridgeReadyListeners = new ArrayList<>();

    public static ElectrodeBridge createElectrodeBridge(ReactApplicationContext reactApplicationContext) {
        synchronized (ElectrodeBridgeHolder.class) {
            if (sElectrodeBridge == null) {
                sElectrodeBridge = new ElectrodeBridge(reactApplicationContext);
                for (OnBridgeReadyListener listener : onBridgeReadyListeners) {
                    listener.onBridgeReady(sElectrodeBridge);
                }
                onBridgeReadyListeners.clear();
            }
        }
        return sElectrodeBridge;
    }

    public interface OnBridgeReadyListener {
        void onBridgeReady(ElectrodeBridge electrodeBridge);
    }

    public static void setOnBridgeReadyListener(@NonNull OnBridgeReadyListener listener) {
        // If bride is already ready, just notify listener immediately
        if (sElectrodeBridge != null) {
            listener.onBridgeReady(sElectrodeBridge);
            return;
        }
        // Otherwise add it to the list of listeners
        onBridgeReadyListeners.add(listener);
    }
}
