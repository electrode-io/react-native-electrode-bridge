package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.walmartlabs.electrode.reactnative.bridge.BridgeMessage;
import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class contains utility methods for bridge
 */

public final class BridgeArguments {

    private static final String TAG = BridgeArguments.class.getSimpleName();

    /**
     * @param data    {@link ReadableMap} received from JS side.
     * @param dataKey Data entry key
     * @return Bundle, if an entry is not found for dataKey
     */
    @NonNull
    public static Bundle responseBundle(@NonNull ReadableMap data, @NonNull String dataKey) {
        final String responseKey = BridgeMessage.Type.RESPONSE.getKey();
        Bundle bundle = new Bundle();
        if (data.getType(dataKey) == null) {
            throw new IllegalArgumentException("Given readable map doesn't have expected entry with key(" + dataKey + ")");
        }
        switch (data.getType(dataKey)) {
            case Array: {
                ReadableArray readableArray = data.getArray(dataKey);
                if (readableArray.size() != 0) {
                    switch (readableArray.getType(0)) {
                        case String:
                            bundle.putStringArray(responseKey, ArgumentsEx.toStringArray(readableArray));
                            break;
                        case Boolean:
                            bundle.putBooleanArray(responseKey, ArgumentsEx.toBooleanArray(readableArray));
                            break;
                        case Number:
                            // Can be int or double
                            bundle.putDoubleArray(responseKey, ArgumentsEx.toDoubleArray(readableArray));
                            break;
                        case Map:
                            bundle.putParcelableArray(responseKey, ArgumentsEx.toBundleArray(readableArray));
                            break;
                        case Array:
                        default:
                            throw new UnsupportedOperationException("Don't support conversion of this type(" + readableArray.getType(0) + ") yet");
                    }
                }
            }
            break;
            case Map:
                bundle.putBundle(responseKey, ArgumentsEx.toBundle(data.getMap(dataKey)));
                break;
            case Boolean:
                bundle.putBoolean(responseKey, data.getBoolean(dataKey));
                break;
            case Number:
                // can be int or double
                bundle.putDouble(responseKey, data.getDouble(dataKey));
                break;
            case String:
                bundle.putString(responseKey, data.getString(dataKey));
                break;
            case Null:
                break;
            default:
                throw new UnsupportedOperationException("Don't support conversion of this type(" + data.getType(dataKey) + ") yet");
        }
        return bundle;
    }

    /**
     * @param object
     * @param type
     * @return Bundle representation of the given object. If the passed object is a primitive wrapper a bundle with one item will be generated and the
     */
    @NonNull
    public static Bundle generateBundle(@Nullable Object object, @NonNull BridgeMessage.Type type) {
        if (object == null) {
            return Bundle.EMPTY;
        }
        Bundle data;
        if (object instanceof Bridgeable) {
            data = new Bundle();
            data.putBundle(BridgeMessage.BRIDGE_MSG_DATA, ((Bridgeable) object).toBundle());
        } else {
            data = BridgeArguments.getBundleForPrimitive(object, object.getClass(), type);
        }

        return data;
    }

    @Nullable
    public static <T> T generateObject(@Nullable Bundle payload, @NonNull Class<T> returnClass, @NonNull BridgeMessage.Type type) {
        T response = null;
        if (payload != null
                && !payload.isEmpty()) {
            String key = BridgeMessage.BRIDGE_MSG_DATA;

            if (payload.get(key) == null) {
                throw new IllegalArgumentException("Cannot find key(" + key + ") in given bundle:" + payload);
            }

            if (Bridgeable.class.isAssignableFrom(returnClass)) {

                if (payload.getBundle(key) == null) {
                    throw new IllegalArgumentException("Value for key(" + key + ") should be a bundle, looks like it is not.");
                }

                response = BridgeArguments.bridgeableFromBundle(payload.getBundle(key), returnClass);
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
    static Object getPrimitiveFromBundle(@NonNull Bundle payload, @NonNull Class reqClazz, @NonNull BridgeMessage.Type type) {
        Object value = null;
        String key = BridgeMessage.BRIDGE_MSG_DATA;
        if (String.class.isAssignableFrom(reqClazz)) {
            value = payload.getString(key);
        } else if (Integer.class.isAssignableFrom(reqClazz)) {
            value = payload.getInt(key);
        } else if (Boolean.class.isAssignableFrom(reqClazz)) {
            value = payload.getBoolean(key);
        } else if (String[].class.isAssignableFrom(reqClazz)) {
            value = payload.getStringArray(key);
        }

        if (reqClazz.isInstance(value)) {
            return value;
        } else {
            throw new IllegalArgumentException("Should never happen, looks like logic to handle " + reqClazz + " is not implemented yet");
        }
    }

    @NonNull
    @VisibleForTesting
    static Bundle getBundleForPrimitive(@NonNull Object respObj, @NonNull Class respClass, BridgeMessage.Type type) {
        Bundle bundle = new Bundle();
        String key = BridgeMessage.BRIDGE_MSG_DATA;
        if (String.class.isAssignableFrom(respClass)) {
            bundle.putString(key, (String) respObj);
        } else if (Integer.class.isAssignableFrom(respClass)) {
            bundle.putInt(key, (Integer) respObj);
        } else if (Boolean.class.isAssignableFrom(respClass)) {
            bundle.putBoolean(key, (Boolean) respObj);
        } else if (String[].class.isAssignableFrom(respClass)) {
            bundle.putStringArray(key, (String[]) respObj);
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
}
