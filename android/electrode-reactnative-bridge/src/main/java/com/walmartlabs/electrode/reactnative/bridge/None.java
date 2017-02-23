package com.walmartlabs.electrode.reactnative.bridge;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Class that is used to represent an emplty request or response type.
 */

public class None implements Bridgeable {


    @NonNull
    @Override
    public Bundle toBundle() {
        return Bundle.EMPTY;
    }

    public None(Bundle emptyBundle) {

    }
}
