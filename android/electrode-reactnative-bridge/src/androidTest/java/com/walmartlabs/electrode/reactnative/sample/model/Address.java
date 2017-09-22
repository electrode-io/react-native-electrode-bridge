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

package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;

public class Address implements Bridgeable, Parcelable {

    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String state;
    private final String zipcode;

    private Address(Builder builder) {
        this.addressLine1 = builder.addressLine1;
        this.addressLine2 = builder.addressLine2;
        this.city = builder.city;
        this.state = builder.state;
        this.zipcode = builder.zipcode;
    }

    public Address(@NonNull Bundle bundle) {
        if (bundle.get("addressLine1") == null) {
            throw new IllegalArgumentException("addressLine1 property is required");
        }
        if (bundle.get("zipcode") == null) {
            throw new IllegalArgumentException("zipcode property is required");
        }

        this.addressLine1 = bundle.getString("addressLine1");
        this.addressLine2 = bundle.getString("addressLine2");
        this.city = bundle.getString("city");
        this.state = bundle.getString("state");
        this.zipcode = bundle.getString("zipcode");
    }

    protected Address(Parcel in) {
        this(in.readBundle());
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressLine1);
        dest.writeString(addressLine2);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zipcode);
    }

    @NonNull
    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("addressLine1", addressLine1);

        if (addressLine2 != null) {
            bundle.putString("addressLine2", addressLine2);
        }

        if (city != null) {
            bundle.putString("city", city);
        }

        if (state != null) {
            bundle.putString("state", state);
        }
        bundle.putString("zipcode", zipcode);

        return bundle;
    }

    public static class Builder {

        private final String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private final String zipcode;

        public Builder(@NonNull String addressLine1, @NonNull String zipcode) {
            this.addressLine1 = addressLine1;
            this.zipcode = zipcode;
        }

        public Builder line2(@Nullable String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public Builder city(@Nullable String city) {
            this.city = city;
            return this;
        }

        public Builder state(@Nullable String state) {
            this.state = state;
            return this;
        }

        public Address build() {
            return new Address(this);
        }


    }

}
