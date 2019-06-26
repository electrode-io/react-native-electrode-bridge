package com.walmartlabs.electrode.reactnative.bridge.util;

import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.BaseBridgeTestCase;
import com.walmartlabs.electrode.reactnative.bridge.helpers.ArgumentsEx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ArgumentsExTests extends BaseBridgeTestCase {

    @Test
    public void toBundleNull() {
        assertEquals(0, ArgumentsEx.toBundle(null).size());
    }

    @Test
    public void toBundleEmpty() {
        WritableMap readableMap = Arguments.createMap();
        assertEquals(0, ArgumentsEx.toBundle(readableMap).size());
    }

    @Test
    public void toBundleEmptyArray() {
        WritableMap readableMap = Arguments.createMap();
        readableMap.putArray("test", Arguments.createArray());
        assertEquals(0, ArgumentsEx.toBundle(readableMap).size());
    }

    @Test
    public void toBundleNullKeyValue() {
        WritableMap readableMap = Arguments.createMap();
        readableMap.putArray("test", null);
        Bundle outBundle = ArgumentsEx.toBundle(readableMap);
        assertEquals(1, outBundle.size());
    }

    @Test
    public void toBundleStringArray() {
        final String STRING_ARRAY = "stringArray";
        WritableMap readableMap = Arguments.createMap();
        WritableArray stringArray = Arguments.createArray();
        stringArray.pushString("1");
        stringArray.pushString("2");
        stringArray.pushString("3");
        stringArray.pushString("4");

        readableMap.putArray(STRING_ARRAY, stringArray);
        Bundle outBundle = ArgumentsEx.toBundle(readableMap);
        assertEquals(1, outBundle.size());
        assertNotNull(outBundle.getStringArray(STRING_ARRAY));
        String[] outArray = outBundle.getStringArray(STRING_ARRAY);
        assertEquals(readableMap.getArray(STRING_ARRAY).size(), outArray.length);
    }

    @Test
    public void toBundleBooleanArray() {
        final String BOOLEAN_ARRAY = "booleanArray";
        WritableMap readableMap = Arguments.createMap();
        WritableArray booleanArray = Arguments.createArray();
        booleanArray.pushBoolean(true);
        booleanArray.pushBoolean(true);
        booleanArray.pushBoolean(true);
        booleanArray.pushBoolean(true);

        readableMap.putArray(BOOLEAN_ARRAY, booleanArray);
        Bundle outBundle = ArgumentsEx.toBundle(readableMap);
        assertEquals(1, outBundle.size());
        assertNotNull(outBundle.getBooleanArray(BOOLEAN_ARRAY));
        boolean[] outArray = outBundle.getBooleanArray(BOOLEAN_ARRAY);
        ReadableArray expectedArray = readableMap.getArray(BOOLEAN_ARRAY);
        assertEquals(expectedArray.size(), outArray.length);

        for (int i = 0; i < expectedArray.size(); i++) {
            boolean expected = expectedArray.getBoolean(i);
            assertEquals(expected, outArray[i]);
        }
    }

    @Test
    public void toBundleNumberArray() {
        final String NUMBER_ARRAY = "numberArray";
        WritableMap readableMap = Arguments.createMap();
        WritableArray numberArray = Arguments.createArray();
        numberArray.pushInt(1);
        numberArray.pushInt(2);
        numberArray.pushInt(3);
        numberArray.pushInt(4);

        readableMap.putArray(NUMBER_ARRAY, numberArray);
        Bundle outBundle = ArgumentsEx.toBundle(readableMap);
        assertEquals(1, outBundle.size());
        double[] outArray = outBundle.getDoubleArray(NUMBER_ARRAY);
        assertNotNull(outArray);
        ReadableArray expectedArray = readableMap.getArray(NUMBER_ARRAY);
        assertEquals(expectedArray.size(), outArray.length);

        for (int i = 0; i < expectedArray.size(); i++) {
            int expected = expectedArray.getInt(i);
            assertEquals(expected, outArray[i], 0);
        }
    }

    @Test
    public void toBundleMap() {
        final String MAP = "map";
        WritableMap readableMap = Arguments.createMap();
        WritableMap readableMapData = Arguments.createMap();
        readableMapData.putArray("nullKey", null);
        readableMapData.putString("string", "some string");
        readableMapData.putInt("int", 1);
        readableMapData.putBoolean("bool", true);
        readableMap.putMap(MAP, readableMapData);
        Bundle outBundle = ArgumentsEx.toBundle(readableMap);
        assertEquals(1, outBundle.size());

        Bundle innerBundle = outBundle.getBundle(MAP);
        assertNotNull(innerBundle);
        assertNull(innerBundle.get("nullKey"));
        assertEquals("some string", innerBundle.getString("string"));
        assertEquals(1, innerBundle.getDouble("int"), 0);
        assertTrue(innerBundle.getBoolean("bool"));
    }
}
