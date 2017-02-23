package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provide methods to report response for a request.
 */
public interface ElectrodeBridgeResponseListener<TRsp> {
    /**
     * Error response
     *
     * @param failureMessage {@link FailureMessage} with failure details.
     */
    void onFailure(@NonNull FailureMessage failureMessage);

    /**
     * Successful response
     *
     * @param responseData response object{@link TRsp}
     */
    void onSuccess(@Nullable TRsp responseData);
}
