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
    ElectrodeBridgeTransceiver electrodeBridgeTransceiver;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        this.electrodeBridgeTransceiver = ElectrodeBridgeTransceiver.create(reactContext);
        modules.add(electrodeBridgeTransceiver);
        return modules;
    }

    // Do not annotate the method with @Override
    // We want to provide backward compatibility starting react-native version 0.42
    // Breaking change in react-native version 0.47 : Android Remove unused createJSModules calls
    // Find more information here : https://github.com/facebook/react-native/releases/tag/v0.47.2
    // https://github.com/facebook/react-native/commit/ce6fb337a146e6f261f2afb564aa19363774a7a8
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
        this.electrodeBridgeTransceiver.onReactNativeInitialized();
    }

}
