package com.walmartlabs.electrode.reactnative.bridge.helpers;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import javax.annotation.Nullable;

// Contains methods to work with arrays that are not supported out of the box by react-native
// Arguments class
public class ArgumentsEx {

    public static double[] toDoubleArray(@NonNull ReadableArray readableArray) {
        double[] result = new double[readableArray.size()];
        for (int i = 0; i < readableArray.size(); i++) {
            result[i] = readableArray.getDouble(i);
        }
        return result;
    }

    public static String[] toStringArray(@NonNull ReadableArray readableArray) {
        String[] result = new String[readableArray.size()];
        for (int i = 0; i < readableArray.size(); i++) {
            result[i] = readableArray.getString(i);
        }
        return result;
    }

    public static boolean[] toBooleanArray(@NonNull ReadableArray readableArray) {
        boolean[] result = new boolean[readableArray.size()];
        for (int i = 0; i < readableArray.size(); i++) {
            result[i] = readableArray.getBoolean(i);
        }
        return result;
    }

    /**
     * Convert a {@link WritableMap} to a {@link Bundle}.
     *
     * @param readableMap the {@link WritableMap} to convert.
     * @return the converted {@link Bundle}.
     */
    @Nullable
    public static Bundle toBundle(@Nullable ReadableMap readableMap) {
        if (readableMap == null) {
            return null;
        }

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

        Bundle bundle = new Bundle();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType readableType = readableMap.getType(key);
            switch (readableType) {
                case Null:
                    bundle.putString(key, null);
                    break;
                case Boolean:
                    bundle.putBoolean(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    // Can be int or double.
                    bundle.putDouble(key, readableMap.getDouble(key));
                    break;
                case String:
                    bundle.putString(key, readableMap.getString(key));
                    break;
                case Map:
                    bundle.putBundle(key, toBundle(readableMap.getMap(key)));
                    break;
                case Array: {
                    ReadableArray readableArray = readableMap.getArray(key);
                    switch (readableArray.getType(0)) {
                        case String:
                            bundle.putStringArray(key, ArgumentsEx.toStringArray(readableArray));
                            break;
                        case Boolean:
                            bundle.putBooleanArray(key, ArgumentsEx.toBooleanArray(readableArray));
                            break;
                        case Number:
                            // Can be int or double but we just assume double for now
                            bundle.putDoubleArray(key, ArgumentsEx.toDoubleArray(readableArray));
                            break;
                        case Map:
                        case Array:
                            break;
                    }
                }
                break;
                default:
                    throw new IllegalArgumentException("Could not convert object with key: " + key + ".");
            }
        }

        return bundle;
    }

}

