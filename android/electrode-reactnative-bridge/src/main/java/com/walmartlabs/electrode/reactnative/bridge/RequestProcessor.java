package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;
import com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments;

import java.util.ArrayList;
import java.util.List;

/**
 * This class takes care of processing a given request when {@link #execute()} is called on the instance.
 *
 * @param <TReq>
 * @param <TResp>
 */
public class RequestProcessor<TReq, TResp> implements Processor {
    private final String TAG = RequestProcessor.class.getSimpleName();

    private final String requestName;
    private final TReq requestPayload;
    private final Class<TResp> responseClass;
    private final Class responseType;//Used when the TResp is List
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
        Bundle data = BridgeArguments.generateDataBundle(requestPayload);

        ElectrodeBridgeRequest req = new ElectrodeBridgeRequest.Builder(requestName)
                .withData(data)
                .build();

        ElectrodeBridgeHolder.sendRequest(req, new ElectrodeBridgeResponseListener<Bundle>() {
            @Override
            public void onFailure(@NonNull FailureMessage failureMessage) {
                responseListener.onFailure(failureMessage);
            }

            @Override
            public void onSuccess(@Nullable Bundle responseData) {
                TResp response = (TResp) BridgeArguments.generateObject(responseData, responseType);

                //Check to see if the List needs Number polishing since the JS side always gives Double for a Number.
                if (response instanceof List
                        && !((List) response).isEmpty()
                        && !responseType.getClass().isAssignableFrom(((List) response).get(0).getClass())//Make sure the expected type and actual type are not same
                        && Number.class.isAssignableFrom(((List) response).get(0).getClass())
                        && Number.class.isAssignableFrom(responseType)) {
                    response = (TResp) updateResponse((List) response);

                }

                runValidationForListResponse(response);
                Logger.d(TAG, "Request processor received the final response(%s) for request(%s)", response, requestName);
                responseListener.onSuccess(response);
            }
        });

    }

    //Needed since any response that is coming back from JS will only have number.
    private List updateResponse(List<Number> response) {
        if (responseType == Double.class) {
            return response;
        }


        List<Number> updatedResponse = new ArrayList<>(response.size());
        for (Number number : response) {
            if (responseType == Integer.class) {
                updatedResponse.add(number.intValue());
            } else {
                throw new IllegalArgumentException("FIXME, add support for " + responseType);
            }
        }
        return updatedResponse;
    }

    private void runValidationForListResponse(TResp response) {
        if (response instanceof List) {
            //Ensure the list content is matching the responseType. This is a workaround to eliminate the limitation of generics preventing the List type being represented inside Class.
            if (!((List) response).isEmpty()) {
                if (!responseType.isAssignableFrom(((List) response).get(0).getClass())) {
                    throw new IllegalArgumentException("Expected List<" + responseType + "> but received List<" + ((List) response).get(0).getClass().getSimpleName() + ">");
                }
            }
        }
    }
}
