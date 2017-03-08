package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

/**
 * Class that handles a native request handler.
 * This class is responsible for converting the received bundle to a full fledged object before sending the request to the registered request handler
 * and also takes care of converting the response object to bundle.
 *
 * @param <TReq>
 * @param <TResp>
 */
public class RequestHandlerProcessor<TReq, TResp> {
    private final String TAG = RequestHandlerProcessor.class.getSimpleName();

    private final String requestName;
    private final Class<TReq> reqClazz;
    private final Class<TResp> respClazz;
    private final ElectrodeBridgeRequestHandler<TReq, TResp> handler;

    public RequestHandlerProcessor(@NonNull String requestName, @NonNull Class<TReq> reqClazz, @NonNull Class<TResp> respClazz, @NonNull ElectrodeBridgeRequestHandler<TReq, TResp> handler) {
        this.requestName = requestName;
        this.reqClazz = reqClazz;
        this.respClazz = respClazz;
        this.handler = handler;
    }

    public void execute() {
        final ElectrodeBridgeRequestHandler<Bundle, Bundle> intermediateRequestHandler = new ElectrodeBridgeRequestHandler<Bundle, Bundle>() {

            @Override
            public void onRequest(@Nullable Bundle payload, @NonNull final ElectrodeBridgeResponseListener<Bundle> responseListener) {
                Logger.d(TAG, "inside onRequest of RequestHandlerProcessor, with payload(%s)", payload);
                TReq request = BridgeArguments.generateObject(payload, reqClazz, BridgeMessage.Type.REQUEST);

                Logger.d(TAG, "Generated request(%s) from payload(%s) and ready to pass to registered handler", request, payload);

                handler.onRequest(request, new ElectrodeBridgeResponseListener<TResp>() {
                    @Override
                    public void onFailure(@NonNull FailureMessage failureMessage) {
                        responseListener.onFailure(failureMessage);
                    }

                    @Override
                    public void onSuccess(TResp obj) {
                        Logger.d(TAG, "Received successful response(%s) from handler, now lets try to convert to real object for the response listener", obj);
                        responseListener.onSuccess(BridgeArguments.generateBundle(obj, BridgeMessage.Type.RESPONSE));
                    }
                });
            }
        };
        ElectrodeBridgeHolder.registerRequestHandler(requestName, intermediateRequestHandler);
    }

}
