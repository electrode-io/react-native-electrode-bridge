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

import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.getNumberValue;

public class BirthYear implements Parcelable, Bridgeable {

    private Integer month;
    private Integer year;

    private BirthYear() {
    }

    private BirthYear(Builder builder) {
        this.month = builder.month;
        this.year = builder.year;
    }

    private BirthYear(Parcel in) {
        this(in.readBundle());
    }

    public BirthYear(@NonNull Bundle bundle) {
        if (bundle.get("month") == null) {
            throw new IllegalArgumentException("month property is required");
        }
        if (bundle.get("year") == null) {
            throw new IllegalArgumentException("year property is required");
        }
        this.month = getNumberValue(bundle, "month") == null ? null : getNumberValue(bundle, "month").intValue();
        this.year = getNumberValue(bundle, "year") == null ? null : getNumberValue(bundle, "year").intValue();
    }

    public static final Creator<BirthYear> CREATOR = new Creator<BirthYear>() {
        @Override
        public BirthYear createFromParcel(Parcel in) {
            return new BirthYear(in);
        }

        @Override
        public BirthYear[] newArray(int size) {
            return new BirthYear[size];
        }
    };

    @NonNull
    public Integer getMonth() {
        return month;
    }

    @NonNull
    public Integer getYear() {
        return year;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(toBundle());
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("month", month);
        bundle.putInt("year", year);
        return bundle;
    }

    public static class Builder {
        private final Integer month;
        private final Integer year;

        public Builder(@NonNull Integer month, @NonNull Integer year) {
            this.month = month;
            this.year = year;
        }

        @NonNull
        public BirthYear build() {
            return new BirthYear(this);
        }
    }
}