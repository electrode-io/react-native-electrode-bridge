/*
 * Copyright 2017 WalmartLabs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
