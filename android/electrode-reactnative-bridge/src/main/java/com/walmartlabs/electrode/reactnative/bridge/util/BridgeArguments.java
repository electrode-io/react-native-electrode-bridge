package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class contains utility methods for bridge
 */

public class BridgeArguments {

    private static final String TAG = BridgeArguments.class.getSimpleName();

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

    /**
     * Returns a bundle representation of the passed object. Object should be a primitive wrapper or {@link Bridgeable}
     *
     * @param requestPayload
     * @return Bundle, if the requestPayload is null {@link Bundle#EMPTY} will be returned
     */
    @NonNull
    public static Bundle generateRequestBundle(@Nullable Object requestPayload) {
        if (requestPayload == null) {
            return Bundle.EMPTY;
        }

        Bundle data;
        if (requestPayload instanceof Bridgeable) {
            data = ((Bridgeable) requestPayload).toBundle();
        } else {
            data = BridgeArguments.getBundleFromPrimitiveForRequest(requestPayload, requestPayload.getClass());
        }

        return data;
    }


    /**
     * Forms T from a given bundle.
     *
     * @param responseBundle     {@link Bundle}
     * @param responseObjectType responseObjectTypeClass
     * @param <T>                responseObjectType
     * @return
     */
    @Nullable
    public static <T> T responseObjectFromBundle(@Nullable Bundle responseBundle, @NonNull Class<T> responseObjectType) {
        T response = null;
        if (responseBundle != null
                && !responseBundle.isEmpty()) {
            if (Bridgeable.class.isAssignableFrom(responseObjectType)) {
                response = BridgeArguments.bridgeableFromBundle(responseBundle, responseObjectType);
            } else {
                response = (T) BridgeArguments.getPrimitiveFromBundleForResponse(responseBundle, responseObjectType);
            }
        }
        return response;
    }


    @Nullable
    public static <T> T bridgeableFromBundle(@NonNull Bundle bundle, @NonNull Class<T> clazz) {
        Logger.d(TAG, "entering bridgeableFromBundle with bundle(%s) for class(%s)", bundle, clazz);

        if (!bundle.containsKey(Bridgeable.KEY_BUNDLE_ID)
                || !(clazz.getSimpleName().equals(bundle.getString(Bridgeable.KEY_BUNDLE_ID)))) {
            Logger.w(TAG, "Looks like the given bundle(%s) does not include KEY_BUNDLE_ID entry, expected(%s) != actual(%s)", bundle, clazz.getSimpleName(), bundle.getString(Bridgeable.KEY_BUNDLE_ID));
            return null;
        }

        try {
            Class clz = Class.forName(clazz.getName());
            Constructor[] constructors = clz.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                if (constructor.getParameterTypes().length == 1) {
                    if (constructor.getParameterTypes()[0].isInstance(bundle)) {
                        Object[] args = new Object[1];
                        args[0] = bundle;
                        Object result = constructor.newInstance(args);
                        if (clazz.isInstance(result)) {
                            return (T) result;
                        } else {
                            Logger.w(TAG, "Object creation from bundle not possible since the created object(%s) is not an instance of %s", result, clazz);
                        }
                    }
                    //Empty constructor available, object construction using bundle can now be attempted.
                    break;
                }
            }
            Logger.w(TAG, "Could not find a constructor that takes in a Bundle param for class(%s)", clazz);
        } catch (ClassNotFoundException e) {
            logException(e);
        } catch (InstantiationException e) {
            logException(e);
        } catch (IllegalAccessException e) {
            logException(e);
        } catch (InvocationTargetException e) {
            logException(e);
        }

        Logger.d(TAG, "FromBundle failed to execute");
        return null;
    }

    private static void logException(Exception e) {
        Logger.w(TAG, "FromBundle failed to execute(%s)", e.getMessage() != null ? e.getMessage() : e.getCause());
    }

    public static Object getPrimitiveFromBundleForRequest(@NonNull Bundle payload, @NonNull Class reqClazz) {
        return getPrimitiveFromBundle(payload, reqClazz, "req");
    }

    public static Object getPrimitiveFromBundleForResponse(@NonNull Bundle payload, @NonNull Class reqClazz) {
        return getPrimitiveFromBundle(payload, reqClazz, "rsp");
    }

    @NonNull
    private static Object getPrimitiveFromBundle(@NonNull Bundle payload, @NonNull Class reqClazz, @NonNull String key) {
        Object value = null;
        if (String.class.isAssignableFrom(reqClazz)) {
            value = payload.getString(key);
        } else if (Integer.class.isAssignableFrom(reqClazz)) {
            value = payload.getInt(key);
        } else if (Boolean.class.isAssignableFrom(reqClazz)) {
            value = payload.getBoolean(key);
        }

        if (reqClazz.isInstance(value)) {
            return value;
        } else {
            throw new IllegalArgumentException("Should never happen, looks like logic to handle " + reqClazz + " is not implemented yet");
        }
    }

    public static Bundle getBundleFromPrimitiveForResponse(@NonNull Object respObj, @NonNull Class respClass) {
        return getBundleForPrimitive(respObj, respClass, "rsp");
    }

    public static Bundle getBundleFromPrimitiveForRequest(@NonNull Object respObj, @NonNull Class respClass) {
        return getBundleForPrimitive(respObj, respClass, "req");
    }

    @NonNull
    private static Bundle getBundleForPrimitive(@NonNull Object respObj, @NonNull Class respClass, String key) {
        Bundle bundle = new Bundle();
        if (String.class.isAssignableFrom(respClass)) {
            bundle.putString(key, (String) respObj);
        } else if (Integer.class.isAssignableFrom(respClass)) {
            bundle.putInt(key, (Integer) respObj);
        } else if (Boolean.class.isAssignableFrom(respClass)) {
            bundle.putBoolean(key, (Boolean) respObj);
        } else {
            throw new IllegalArgumentException("Should never happen, looks like logic to handle " + respClass + " is not implemented yet");
        }
        return bundle;
    }

}
