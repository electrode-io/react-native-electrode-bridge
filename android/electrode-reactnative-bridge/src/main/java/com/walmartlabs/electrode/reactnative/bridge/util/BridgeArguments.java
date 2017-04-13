package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.walmartlabs.electrode.reactnative.bridge.BridgeMessage;
import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;
import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class contains utility methods for bridge
 */

public final class BridgeArguments {

    private static final String TAG = BridgeArguments.class.getSimpleName();

    private static final Set<Class> SUPPORTED_PRIMITIVE_TYPES = new HashSet<Class>() {{
        add(String.class);
        add(String[].class);
        add(Integer.class);
        add(Integer[].class);
        add(int[].class);
        add(Boolean.class);
        add(Boolean[].class);
        add(Double.class);
        add(Double[].class);
        add(double[].class);
        add(Number.class);
    }};

    /**
     * @param object Accepted object types are {@link Bridgeable}, All primitive wrappers and null
     * @return Bundle representation of the given object. The output bundle will put the object inside key = {@link BridgeMessage#BRIDGE_MSG_DATA}
     */
    @NonNull
    public static Bundle generateDataBundle(@Nullable Object object) {
        if (object == null) {
            return Bundle.EMPTY;
        }
        Bundle data = new Bundle();
        if (object instanceof Bridgeable) {
            data.putBundle(BridgeMessage.BRIDGE_MSG_DATA, ((Bridgeable) object).toBundle());
        } else if (object instanceof List) {
            updateBundleWithList((List) object, data);
        } else {
            updateBundleForPrimitive(object, object.getClass(), data);
        }

        return data;
    }

    private static void updateBundleWithList(@NonNull List objList, Bundle bundle) {
        if (!objList.isEmpty()) {
            Object firstItem = null;
            for (Object o : objList) {
                if (o != null) {
                    firstItem = o;
                    break;
                }
            }

            if (firstItem == null) {
                bundle.putParcelableArray(BridgeMessage.BRIDGE_MSG_DATA, new Bundle[0]);
            }

            if (firstItem instanceof Bridgeable) {
                bundle.putParcelableArray(BridgeMessage.BRIDGE_MSG_DATA, bridgeablesToBundleArray(objList));
            } else if (isSupportedPrimitiveType(firstItem.getClass())) {
                if (firstItem instanceof String) {
                    String[] stringArray = (String[]) objList.toArray(new String[objList.size()]);
                    bundle.putStringArray(BridgeMessage.BRIDGE_MSG_DATA, stringArray);
                } else if (firstItem instanceof Integer) {
                    int[] intArray = new int[objList.size()];
                    for (int i = 0; i < objList.size(); i++) {
                        intArray[i] = (Integer) objList.get(i);
                    }
                    bundle.putIntArray(BridgeMessage.BRIDGE_MSG_DATA, intArray);
                } else if (firstItem instanceof Double) {
                    double[] doubleArray = new double[objList.size()];
                    for (int i = 0; i < objList.size(); i++) {
                        doubleArray[i] = (double) objList.get(i);
                    }
                    bundle.putDoubleArray(BridgeMessage.BRIDGE_MSG_DATA, doubleArray);
                } else {
                    throw new IllegalArgumentException("Should never happen, looks like logic to handle " + firstItem.getClass() + " is not implemented yet");
                }
            } else {
                throw new IllegalArgumentException("should never reach here, type" + firstItem.getClass() + " not supported yet");
            }

        } else {
            Logger.d(TAG, "Received empty list, will put empty bundle array for BRIDGE_MSG_DATA");
            bundle.putParcelableArray(BridgeMessage.BRIDGE_MSG_DATA, new Bundle[0]);
        }
    }

    @NonNull
    public static Bundle[] bridgeablesToBundleArray(@NonNull List objList) {
        Bundle[] bundleList = new Bundle[objList.size()];
        for (int i = 0; i < objList.size(); i++) {
            Object obj = objList.get(i);
            if (obj instanceof Bridgeable) {
                bundleList[i] = (((Bridgeable) obj).toBundle());
            } else {
                throw new IllegalArgumentException("Should never reach here, received a non-bridgeable object, " + obj);
            }
        }
        return bundleList;
    }

    /**
     * Looks for an entry with key = {@link BridgeMessage#BRIDGE_MSG_DATA} inside the bundle and then tries to convert the value to either a primitive wrapper or {@link Bridgeable}
     *
     * @param data        {@link Object} data that may need to be casted to a specific type. Mainly for complex objects returned by React will be in form of Bundle.
     * @param returnClass {@link Class} expected return type of the Object
     * @return Object
     */
    @Nullable
    public static Object generateObject(@Nullable Object data, @NonNull Class<?> returnClass) {

        if (data == null) {
            return null;
        }

        if (returnClass.isAssignableFrom(data.getClass())) {
            Logger.d(TAG, "Object conversion not required since the data is already of type(%s)", returnClass);
            return data;
        }

        Object response;
        if (data instanceof List) {
            //When native sends a request the data will already be a list, does not require  a conversion
            response = data;
        } else if (isArray(data)) {
            response = getList(data, returnClass);
        } else if (data instanceof Bundle) {
            response = objectFromBundle((Bundle) data, returnClass);
        } else if (isSupportedPrimitiveType(data.getClass())) {
            //noinspection unchecked
            response = data;
        } else {
            throw new IllegalArgumentException("Should never happen, looks like logic to handle " + data.getClass() + " type is not implemented yet, returnClass:" + returnClass);
        }
        return response;
    }


    private static boolean isArray(@NonNull Object obj) {
        return obj.getClass().isArray();
    }

    /**
     * @param obj           input array
     * @param listItemClass Defines the conent type of the list
     * @return List
     */
    public static List getList(Object obj, @Nullable Class listItemClass) {
        if (!isArray(obj)) {
            throw new IllegalArgumentException("Should never reach here, expected an array, received: " + obj);
        }

        List<Object> convertedList = new ArrayList<>();
        if (obj instanceof Bundle[]) {
            if (listItemClass == null) {
                throw new IllegalArgumentException("listItemClass is required to convert Bundle[]");
            }
            Bundle[] bundles = (Bundle[]) obj;

            for (Bundle bundle : bundles) {
                Object item = BridgeArguments.objectFromBundle(bundle, listItemClass);
                convertedList.add(item);
            }

        } else if (Object[].class.isAssignableFrom(obj.getClass())
                && isSupportedPrimitiveType(obj.getClass())) {
            Collections.addAll(convertedList, (Object[]) obj);
        } else if (int[].class.isAssignableFrom(obj.getClass())
                && isSupportedPrimitiveType(obj.getClass())) {
            int[] objectArray = (int[]) obj;
            for (Object o : objectArray) {
                convertedList.add(o);
            }

        } else if (double[].class.isAssignableFrom(obj.getClass())
                && isSupportedPrimitiveType(obj.getClass())) {
            double[] objectArray = (double[]) obj;
            for (Object o : objectArray) {
                convertedList.add(o);
            }

        } else {
            throw new IllegalArgumentException("Array of type " + obj.getClass().getSimpleName() + " is not supported yet");
        }
        return convertedList;
    }

    @VisibleForTesting
    @NonNull
    static Object objectFromBundle(@NonNull Bundle bundle, @NonNull Class<?> clazz) {
        Logger.d(TAG, "entering objectFromBundle with bundle(%s) for class(%s)", bundle, clazz);

        //noinspection TryWithIdenticalCatches
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
                            //noinspection unchecked
                            return result;
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
        } catch (@SuppressWarnings("TryWithIdenticalCatches") InstantiationException e) {
            logException(e);
        } catch (IllegalAccessException e) {
            logException(e);
        } catch (InvocationTargetException e) {
            logException(e);
        }

        throw new IllegalArgumentException("Unable to generate a Bridgeable from bundle: " + bundle);
    }

    private static void logException(Exception e) {
        Logger.w(TAG, "FromBundle failed to execute(%s)", e.getMessage() != null ? e.getMessage() : e.getCause());
    }

    @NonNull
    @VisibleForTesting
    static Bundle updateBundleForPrimitive(@NonNull Object respObj, @NonNull Class respClass, @NonNull Bundle bundle) {
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

    /**
     * Converts a list of {@link Integer} to int[], any null value will be replaced with 0 inside the array
     *
     * @param integerList {@link List<Integer>}
     * @return int[]
     */
    public static int[] toIntArray(@NonNull List<Integer> integerList) {
        int array[] = new int[integerList.size()];

        for (int i = 0; i < integerList.size(); i++) {
            if (integerList.get(i) != null) {
                array[i] = integerList.get(i);
            }
        }
        return array;

    }

    public static List<Integer> toIntegerList(@Nullable int[] intArray) {
        List<Integer> result = new ArrayList<>();

        if (intArray != null) {
            for (int val : intArray) {
                result.add(val);
            }
        }
        return result;
    }

    private static boolean isSupportedPrimitiveType(@NonNull Class clazz) {
        return SUPPORTED_PRIMITIVE_TYPES.contains(clazz);
    }
}
