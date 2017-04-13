package com.walmartlabs.electrode.reactnative.bridge;

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
public class RequestProcessor<TReq, TResp> extends BridgeProcessor {
    private final String TAG = RequestProcessor.class.getSimpleName();

    private final String requestName;
    private final TReq requestPayload;
    private final Class<TResp> responseClass;
    private final Class responseType;//Used when the TResp is List, represents the content type of the list. For non list, the response class and responseType will be same.
    private final ElectrodeBridgeResponseListener<TResp> responseListener;

    public RequestProcessor(@NonNull String requestName, @Nullable TReq requestPayload, @NonNull Class<TResp> respClass, @NonNull ElectrodeBridgeResponseListener<TResp> responseListener) {
        this(requestName, requestPayload, respClass, respClass, responseListener);
    }

    public RequestProcessor(@NonNull String requestName, @Nullable TReq requestPayload, @NonNull Class<TResp> respClass, @NonNull Class responseType, @NonNull ElectrodeBridgeResponseListener<TResp> responseListener) {
        this.requestName = requestName;
        this.requestPayload = requestPayload;
        this.responseClass = respClass;
        this.responseType = responseType;
        this.responseListener = responseListener;
    }


    @Override
    public void execute() {
        Logger.d(TAG, "Request processor started processing request(%s)", requestName);
        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(requestName)
                .withData(requestPayload)
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<ElectrodeBridgeResponse>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                responseListener.onFailure(failureMessage);
            }

            @Override
            public void onSuccess(@Nullable ElectrodeBridgeResponse bridgeResponse) {
                if (bridgeResponse == null) {
                    throw new IllegalArgumentException("BridgeResponse cannot be null, should never reach here");
                }

                TResp response = null;
                if (responseClass != None.class) {
                    response = (TResp) BridgeArguments.generateObject(bridgeResponse.getData(), responseType);
                }

                Logger.d(TAG, "Request processor received the final response(%s) for request(%s)", response, requestName);
                responseListener.onSuccess(response);
            }
        });

    }
}
