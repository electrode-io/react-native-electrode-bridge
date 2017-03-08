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

    private static final String TAG = BridgeMessage.class.getSimpleName();

    static final String BRIDGE_MSG_NAME = "name";
    static final String BRIDGE_MSG_ID = "id";
    static final String BRIDGE_MSG_TYPE = "type";
    static final String BRIDGE_MSG_DATA = "data";

    private final String name;
    private final String id;
    private final BridgeArguments.Type type;
    private final Bundle data;


    public BridgeMessage(@NonNull String name, @NonNull String id, @NonNull BridgeArguments.Type type, @Nullable Bundle data) {
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
    public BridgeArguments.Type getType() {
        return type;
    }

    @Nullable
    public Bundle getData() {
        return data;
    }

    @NonNull
    public WritableMap map() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(BRIDGE_MSG_ID, getId());
        writableMap.putString(BRIDGE_MSG_NAME, getName());

        Bundle data = getData() != null ? getData() : Bundle.EMPTY;
        writableMap.putMap(BRIDGE_MSG_DATA, Arguments.fromBundle(data));

        writableMap.putString(BRIDGE_MSG_TYPE, getType().getKey());

        return writableMap;
    }

    @Override
    public String toString() {
        return name + ", data: " + (data != null ? data : "<empty>") + " type: " + type;
    }

    static boolean isValid(final ReadableMap data, BridgeArguments.Type type) {
        return isValid(data)
                && BridgeArguments.Type.getType(data.getString(BRIDGE_MSG_TYPE)) == type;
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
