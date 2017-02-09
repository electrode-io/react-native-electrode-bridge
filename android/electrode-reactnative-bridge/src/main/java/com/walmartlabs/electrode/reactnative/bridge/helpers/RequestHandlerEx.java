package com.walmartlabs.electrode.reactnative.bridge.helpers;

/**
 * Extended RequestHandler taking a request payload and returning a response
 *
 * @param <TRsp> The request payload type
 * @param <TRsp> The response payload type
 */
public interface RequestHandlerEx<TReq, TRsp> {
    void handleRequest(TReq payload, Response<TRsp> response);
}
