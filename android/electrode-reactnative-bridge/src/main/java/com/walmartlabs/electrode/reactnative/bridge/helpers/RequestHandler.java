package com.walmartlabs.electrode.reactnative.bridge.helpers;

import com.walmartlabs.electrode.reactnative.bridge.ElectrodeBridgeResponseListener;

/**
 * Basic RequestHandler taking no request payload and returning a response
 *
 * @param <TRsp> The response payload type
 */
public interface RequestHandler<TRsp> {
    void handleRequest(ElectrodeBridgeResponseListener<TRsp> response);
}
