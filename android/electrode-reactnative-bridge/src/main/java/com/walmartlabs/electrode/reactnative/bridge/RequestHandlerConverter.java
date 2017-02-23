package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

public class RequestHandlerConverter<TReq, TResp> implements ElectrodeBridgeRequestHandler<Bundle, Bundle> {
    private final String TAG = RequestHandlerConverter.class.getSimpleName();

    private final Class<TReq> reqClazz;
    private final Class<TResp> respClazz;
    private final ElectrodeBridgeRequestHandler<TReq, TResp> handler;

    public RequestHandlerConverter(@NonNull Class<TReq> reqClazz, @NonNull Class<TResp> respClazz, @NonNull ElectrodeBridgeRequestHandler<TReq, TResp> handler) {
        this.reqClazz = reqClazz;
        this.respClazz = respClazz;
        this.handler = handler;
    }

    @Override
    public void onRequest(@Nullable Bundle payload, @NonNull final ElectrodeBridgeResponseListener<Bundle> responseListener) {
        Logger.d(TAG, "inside onRequest of RequestHandlerConverter, with payload(%s)", payload);
        TReq request = null;

        if (payload != null) {
            if (Bridgeable.class.isAssignableFrom(reqClazz)) {
                request = BridgeArguments.bridgeableFromBundle(payload, reqClazz);
            } else {
                Object obj = BridgeArguments.getPrimitiveFromBundleForRequest(payload, reqClazz);
                if (reqClazz.isInstance(obj)) {
                    request = (TReq) obj;
                } else {
                    throw new IllegalArgumentException("The payload type" + payload.getClass() + " is not supported yet.! ");
                }
            }
        }

        Logger.d(TAG, "Generated request(%s) from payload(%s) and ready to pass to registered handler", request, payload);

        handler.onRequest(request, new ElectrodeBridgeResponseListener<TResp>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                responseListener.onFailure(failureMessage);
            }

            @Override
            public void onSuccess(TResp obj) {
                Logger.d(TAG, "Received successful response(%s) from handler, now lets try to convert to real object for the response listener", obj);
                Bundle bundle;
                if (Bridgeable.class.isAssignableFrom(respClazz)) {
                    bundle = ((Bridgeable) obj).toBundle();
                } else {
                    bundle = BridgeArguments.getBundleFromPrimitiveForResponse(obj, respClazz);
                }
                Logger.d(TAG, "Bundle(%s) generated from response(%s) ", bundle, obj);
                responseListener.onSuccess(bundle);
            }
        });
    }
}
