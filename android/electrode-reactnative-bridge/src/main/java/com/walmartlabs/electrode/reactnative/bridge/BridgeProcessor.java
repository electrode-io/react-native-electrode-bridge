package com.walmartlabs.electrode.reactnative.bridge;

import android.support.annotation.NonNull;

import com.walmartlabs.electrode.reactnative.bridge.helpers.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class BridgeProcessor implements Processor {

    private static final String TAG = BridgeProcessor.class.getSimpleName();

    /**
     * Checks to see if the expected response type is a Number and returns Number.class.
     * This is needed since the react side always sends a Double for a Number and the processor does the magic of converting that to an expected type.
     *
     * @param expectedResponseType class
     * @return Class
     */
    Class getResponseType(@NonNull Class expectedResponseType) {
        if (Number.class.isAssignableFrom(expectedResponseType)) {
            return Number.class;
        } else {
            return expectedResponseType;
        }
    }

    /**
     * @param listResponse response object
     * @param listItemType list content type
     * @return List
     */
    //Needed since any response that is coming back from JS will only have number.
    static List updateListResponseIfRequired(List listResponse, @NonNull Class listItemType) {
        if (!listResponse.isEmpty()
                && isNumberAndNeedsConversion(listResponse.get(0), listItemType)) {
            Logger.d(TAG, "Performing list Number conversion from %s to %s", listResponse.get(0).getClass(), listItemType);
            List<Number> updatedResponse = new ArrayList<>(listResponse.size());
            for (Object number : listResponse) {
                updatedResponse.add(convertToNumberToResponseType((Number) number, listItemType));
            }
            return updatedResponse;
        }
        return listResponse;
    }

    static Object updateNumberResponseToMatchReturnType(@NonNull Object response, @NonNull Class responseType) {

        if (isNumberAndNeedsConversion(response, responseType)) {
            Logger.d(TAG, "Performing Number conversion from %s to %s", response.getClass(), responseType);
            return convertToNumberToResponseType((Number) response, responseType);
        } else {
            return response;
        }
    }

    static void runValidationForListResponse(Object response, Class expectedResponseType) {
        if (response instanceof List) {
            //Ensure the list content is matching the responseType. This is a workaround to eliminate the limitation of generics preventing the List type being represented inside Class.
            if (!((List) response).isEmpty()) {
                if (!expectedResponseType.isAssignableFrom(((List) response).get(0).getClass())) {
                    throw new IllegalArgumentException("Expected List<" + expectedResponseType + "> but received List<" + ((List) response).get(0).getClass().getSimpleName() + ">");
                }
            }
        }
    }

    @NonNull
    static private Number convertToNumberToResponseType(@NonNull Number response, @NonNull Class responseType) {
        if (responseType == Integer.class) {
            return response.intValue();
        } else {
            throw new IllegalArgumentException("FIXME, add support for " + responseType);
        }
    }

    static private boolean isNumberAndNeedsConversion(@NonNull Object obj, Class responseType) {
        return !responseType.getClass().isAssignableFrom(obj.getClass())//Make sure the expected type and actual type are not same
                && Number.class.isAssignableFrom(obj.getClass())
                && Number.class.isAssignableFrom(responseType);
    }
}
