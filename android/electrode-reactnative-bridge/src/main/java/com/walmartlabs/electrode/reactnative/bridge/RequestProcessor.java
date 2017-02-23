package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

/**
 * This class takes care of processing a given request when {@link #execute()} is called on the instance.
 *
 * @param <TReq>
 * @param <TResp>
 */
public class RequestProcessor<TReq, TResp> {
    private final String TAG = RequestProcessor.class.getSimpleName();

    private final String requestName;
    private final TReq requestPayload;
    private final Class<TResp> responseClass;
    private final ElectrodeBridgeResponseListener<TResp> responseListener;

    public RequestProcessor(@NonNull String requestName, @Nullable TReq requestPayload, @NonNull Class<TResp> respClass, @NonNull ElectrodeBridgeResponseListener<TResp> responseListener) {
        this.requestName = requestName;
        this.requestPayload = requestPayload;
        this.responseClass = respClass;
        this.responseListener = responseListener;
    }


    public void execute() {
        Logger.d(TAG, "Request processor started processing request(%s)", requestName);
        Bundle data;
        if (requestPayload == null) {
            data = Bundle.EMPTY;
        } else {
            if (requestPayload instanceof Bridgeable) {
                data = ((Bridgeable) requestPayload).toBundle();
            } else {
                data = BridgeArguments.getBundleFromPrimitiveForRequest(requestPayload, requestPayload.getClass());
            }
        }
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(requestName)
                .withData(data)
                .withDispatchMode(ElectrodeBridgeRequest.DispatchMode.NATIVE)//FIXME: remove dispatch mode.
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                responseListener.onFailure(failureMessage);
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                TResp response = null;
                if (responseData != null) {
                    if (Bridgeable.class.isAssignableFrom(responseClass)) {
                        response = BridgeArguments.bridgeableFromBundle(responseData, responseClass);
                    } else {
                        response = (TResp) BridgeArguments.getPrimitiveFromBundleForResponse(responseData, responseClass);
                    }
                }
                Logger.d(TAG, "Request processor received the final response(%s) for request(%s)", response, requestName);
                responseListener.onSuccess(response);
            }
        });

    }

}
