package com.walmartlabs.electrode.reactnative.bridge.helpers;

public interface RequestHandler<T> {
  void handleRequest(Response<T> response);
}
