package com.walmartlabs.electrode.reactnative.bridge;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElectrodeBridgePackage implements ReactPackage {

    @VisibleForTesting
    ElectrodeBridgeInternal electrodeBridgeInternal;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        this.electrodeBridgeInternal = ElectrodeBridgeInternal.create(reactContext);
        modules.add(electrodeBridgeInternal);
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    //
    // Invoked by ern platform once react native engine is initialized/ready
    public void onReactNativeInitialized() {
        this.electrodeBridgeInternal.onReactNativeInitialized();
    }

}
