package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;

/**
 * This class contains utility methods for bridge
 */

public class BridgeArguments {

    /**
     * @param data    {@link ReadableMap} received from JS side.
     * @param dataKey Data entry key
     * @return Bundle, if an entry is not found for dataKey
     */
    @NonNull
    public static Bundle responseBundle(@NonNull ReadableMap data, @NonNull String dataKey) {
        Bundle bundle;
        bundle = new Bundle();
        if (data != null) {
            switch (data.getType(dataKey)) {
                case Array: {
                    ReadableArray readableArray = data.getArray(dataKey);
                    if (readableArray.size() != 0) {
                        switch (readableArray.getType(0)) {
                            case String:
                                bundle.putStringArray("rsp", ArgumentsEx.toStringArray(readableArray));
                                break;
                            case Boolean:
                                bundle.putBooleanArray("rsp", ArgumentsEx.toBooleanArray(readableArray));
                                break;
                            case Number:
                                // Can be int or double
                                bundle.putDoubleArray("rsp", ArgumentsEx.toDoubleArray(readableArray));
                                break;
                            case Map:
                                bundle.putParcelableArray("rsp", ArgumentsEx.toBundleArray(readableArray));
                                break;
                            case Array:
                                // Don't support array of arrays yet
                                break;
                        }
                    }
                }
                break;
                case Map:
                    bundle.putBundle("rsp", ArgumentsEx.toBundle(data.getMap(dataKey)));
                    break;
                case Boolean:
                    bundle.putBoolean("rsp", data.getBoolean(dataKey));
                    break;
                case Number:
                    // can be int or double
                    bundle.putDouble("rsp", data.getDouble(dataKey));
                    break;
                case String:
                    bundle.putString("rsp", data.getString(dataKey));
                    break;
                case Null:
                    break;
            }
        }
        return bundle;
    }

}
