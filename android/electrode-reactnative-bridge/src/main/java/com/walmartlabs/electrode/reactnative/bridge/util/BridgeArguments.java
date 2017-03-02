package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

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
                    bundle = ArgumentsEx.toBundle(data.getMap(dataKey));
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
     * @param object
     * @param type
     * @return Bundle representation of the given object. If the passed object is a primitive wrapper a bundle with one item will be generated and the
     */
    @NonNull
    public static Bundle generateBundle(@Nullable Object object, @NonNull Type type) {
        if (object == null) {
            return Bundle.EMPTY;
        }
        Bundle data;
        if (object instanceof Bridgeable) {
            data = ((Bridgeable) object).toBundle();
        } else {
            data = BridgeArguments.getBundleForPrimitive(object, object.getClass(), type);
        }

        return data;
    }

    @Nullable
    public static <T> T generateObject(@Nullable Bundle payload, @NonNull Class<T> returnClass, @NonNull Type type) {
        T response = null;
        if (payload != null
                && !payload.isEmpty()) {
            if (Bridgeable.class.isAssignableFrom(returnClass)) {
                response = BridgeArguments.bridgeableFromBundle(payload, returnClass);
            } else {
                response = (T) BridgeArguments.getPrimitiveFromBundle(payload, returnClass, type);
            }
        }
        return response;
    }

    @VisibleForTesting
    @Nullable
    static <T> T bridgeableFromBundle(@NonNull Bundle bundle, @NonNull Class<T> clazz) {
        Logger.d(TAG, "entering bridgeableFromBundle with bundle(%s) for class(%s)", bundle, clazz);

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

    @NonNull
    @VisibleForTesting
    static Object getPrimitiveFromBundle(@NonNull Bundle payload, @NonNull Class reqClazz, @NonNull Type type) {
        Object value = null;
        if (String.class.isAssignableFrom(reqClazz)) {
            value = payload.getString(type.key);
        } else if (Integer.class.isAssignableFrom(reqClazz)) {
            value = payload.getInt(type.key);
        } else if (Boolean.class.isAssignableFrom(reqClazz)) {
            value = payload.getBoolean(type.key);
        } else if (String[].class.isAssignableFrom(reqClazz)) {
            value = payload.getStringArray(type.key);
        }

        if (reqClazz.isInstance(value)) {
            return value;
        } else {
            throw new IllegalArgumentException("Should never happen, looks like logic to handle " + reqClazz + " is not implemented yet");
        }
    }

    @NonNull
    @VisibleForTesting
    static Bundle getBundleForPrimitive(@NonNull Object respObj, @NonNull Class respClass, Type type) {
        Bundle bundle = new Bundle();
        if (String.class.isAssignableFrom(respClass)) {
            bundle.putString(type.key, (String) respObj);
        } else if (Integer.class.isAssignableFrom(respClass)) {
            bundle.putInt(type.key, (Integer) respObj);
        } else if (Boolean.class.isAssignableFrom(respClass)) {
            bundle.putBoolean(type.key, (Boolean) respObj);
        } else if (String[].class.isAssignableFrom(respClass)) {
            bundle.putStringArray(type.key, (String[]) respObj);
        } else {
            throw new IllegalArgumentException("Should never happen, looks like logic to handle " + respClass + " is not implemented yet");
        }
        return bundle;
    }

    public static Number getNumberValue(@NonNull Bundle bundle, String key) {
        Number output = null;
        if (bundle != null && bundle.containsKey(key)) {
            Object obj = bundle.get(key);
            if (obj != null) {
                if (obj.getClass().isAssignableFrom(Integer.class)) {
                    output = bundle.getInt(key);
                } else if (obj.getClass().isAssignableFrom(Double.class)) {
                    output = bundle.getDouble(key);
                }
            }
        }
        return output;
    }

    public enum Type {
        REQUEST("req"),
        RESPONSE("rsp"),
        EVENT("event");

        private String key;

        Type(@NonNull String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
