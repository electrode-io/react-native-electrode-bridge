package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Indicates that any class that implements this interface can be sent across the ElectrodeBridge.
 */

public interface Bridgeable {

    /**
     * Returns a bundle representation of your model object.
     *
     * @return Bundle
     */
    @NonNull
    Bundle toBundle();
}
