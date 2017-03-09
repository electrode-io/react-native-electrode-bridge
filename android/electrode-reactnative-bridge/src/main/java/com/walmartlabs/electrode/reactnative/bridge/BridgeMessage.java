package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

import java.util.UUID;

public class BridgeMessage {

    /**
     * Represents the types of arguments that is sent across the bridge.
     */
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

        @Nullable
        public static Type getType(@NonNull String key) {
            for (Type type : Type.values()) {
                if (type.key.equalsIgnoreCase(key)) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final String TAG = BridgeMessage.class.getSimpleName();

    public static final String BRIDGE_MSG_NAME = "name";
    public static final String BRIDGE_MSG_ID = "id";
    public static final String BRIDGE_MSG_TYPE = "type";
    public static final String BRIDGE_MSG_DATA = "data";

    private final String name;
    private final String id;
    private final Type type;
    private final Object data;


    public BridgeMessage(@NonNull String name, @NonNull String id, @NonNull Type type, @Nullable Object data) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.data = data;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    @NonNull
    public WritableMap map() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(BRIDGE_MSG_ID, getId());
        writableMap.putString(BRIDGE_MSG_NAME, getName());

        WritableMap dataMap;
        if (data instanceof Bundle) {
            dataMap = Arguments.fromBundle((Bundle) data);
        } else {
            dataMap = Arguments.fromBundle(BridgeArguments.generateBundle(data, type));
        }
        writableMap.merge(dataMap);

        writableMap.putString(BRIDGE_MSG_TYPE, getType().getKey());
        return writableMap;
    }

    @NonNull
    public Bundle bundle() {
        Bundle bundle = new Bundle();
        bundle.putString(BRIDGE_MSG_ID, getId());
        bundle.putString(BRIDGE_MSG_NAME, getName());

        if (data instanceof Bundle) {
            bundle.putAll((Bundle) data);
        } else {
            bundle.putAll(BridgeArguments.generateBundle(data, type));
        }

        bundle.putString(BRIDGE_MSG_TYPE, getType().getKey());

        return bundle;
    }

    @Override
    public String toString() {
        return name + ", data: " + (data != null ? data : "<empty>") + " type: " + type;
    }

    static boolean isValid(final ReadableMap data, Type type) {
        return isValid(data)
                && Type.getType(data.getString(BRIDGE_MSG_TYPE)) == type;
    }

    static boolean isValid(final ReadableMap data) {
        return data != null
                && data.hasKey(BRIDGE_MSG_NAME)
                && data.hasKey(BRIDGE_MSG_ID)
                && data.hasKey(BRIDGE_MSG_TYPE);
    }

    static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
