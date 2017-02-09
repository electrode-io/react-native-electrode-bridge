package com.walmartlabs.electrode.reactnative.bridge.helpers;

public interface EventListener<T> {
    void onEvent(T obj);
}
