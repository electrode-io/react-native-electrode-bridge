package com.walmartlabs.electrode.reactnative.sample.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;
import com.walmartlabs.electrode.reactnative.sample.model.Status;

public class UpdatePersonRequestData implements Bridgeable {

    private final String firstName;
    private final String lastName;
    private final Status status;

    public UpdatePersonRequestData(@NonNull Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.status = builder.status;
    }

    public UpdatePersonRequestData(@NonNull Bundle bundle) {
        if (bundle.get("firstName") == null) {
            throw new IllegalArgumentException("firstName property is required");
        }
        if (bundle.get("lastName") == null) {
            throw new IllegalArgumentException("lastName property is required");
        }
        this.firstName = bundle.getString("firstName");
        this.lastName = bundle.getString("lastName");
        this.status = bundle.containsKey("status") ? new Status(bundle.getBundle("status")) : null;
    }

    @NonNull
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        if (status != null) {
            bundle.putBundle("status", status.toBundle());
        }

        return bundle;
    }

    @NonNull
    public String getFirstName() {
        return firstName;
    }

    @NonNull
    public String getLastName() {
        return lastName;
    }

    @Nullable
    public Status getStatus() {
        return status;
    }


    public static class Builder {
        private final String firstName;
        private final String lastName;
        private final Status status;

        public Builder(@NonNull String firstName, @NonNull String lastName, @Nullable Status status) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.status = status;
        }

        public UpdatePersonRequestData build() {
            return new UpdatePersonRequestData(this);
        }

    }
}
