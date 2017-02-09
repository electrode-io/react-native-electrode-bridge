package com.walmartlabs.electrode.reactnative.bridge.helpers;

public interface Response<T> {
    void onSuccess(T obj);

    void onError(String code, String message);
}
