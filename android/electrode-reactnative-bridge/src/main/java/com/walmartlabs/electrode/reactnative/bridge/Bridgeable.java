package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Indicates that any calss that implements this interface can be sent acorss the ElectrodeBridge.
 */

public interface Bridgeable {

    String KEY_BUNDLE_ID = "className";

    /**
     * Returns a bundle representation of your model object.
     * <p>
     * The implementor of this method must include the KEY_BUNDLE_ID param inside the bundle with a value of the class's canonical name as in the example below.
     * <p>
     * <code>bundle.putString(KEY_BUNDLE_ID,  <YourClass>.class.getSimpleName());</code>
     *
     * @return Bundle
     */
    @NonNull
    Bundle toBundle();
}
