package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;

public class ElectrodeBridgeHolder {

    private static ElectrodeBridge sElectrodeBridge = null;
    private static ArrayList<OnBridgeReadyListener> onBridgeReadyListeners = new ArrayList<>();

    /**
     * Creates the ElectrodeBridge singleton
     *
     * @param reactApplicationContext The react application context
     * @return The singleton instance of ElectrodeBridge
     */
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

    /**
     * Provides a method to get notified whenever bridge initialization is complete
     */
    public interface OnBridgeReadyListener {
        /**
         * Called when the bridge initialization is complete and brige is ready
         * @param electrodeBridge The bridge instance
         */
        void onBridgeReady(ElectrodeBridge electrodeBridge);
    }

    /**
     * Adds a listener to get notified whenever bridge instance is ready for use
     *
     * @param listener OnBridgeReadyListener instance
     */
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
