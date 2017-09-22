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

import java.util.ArrayList;
import java.util.List;

import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.bridgeablesToBundleArray;
import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.getList;
import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.getNumberValue;
import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.toIntArray;

public class Person implements Parcelable, Bridgeable {

    private String name;
    private Integer age;
    private Integer month;
    private Status status;
    private Position position;
    private BirthYear birthYear;
    private List<Address> addressList;
    private List<String> siblingsNames;
    private List<Integer> siblingsAges;

    private Person() {
    }

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.month = builder.month;
        this.status = builder.status;
        this.position = builder.position;
        this.birthYear = builder.birthYear;
        this.addressList = builder.addressList;
        this.siblingsNames = builder.siblingsNames;
        this.siblingsAges = builder.siblingsAges;
    }

    private Person(Parcel in) {
        this(in.readBundle());
    }

    public Person(@NonNull Bundle bundle) {
        if (bundle.get("name") == null) {
            throw new IllegalArgumentException("name property is required");
        }
        if (bundle.get("month") == null) {
            throw new IllegalArgumentException("month property is required");
        }
        this.name = bundle.getString("name");
        this.age = getNumberValue(bundle, "age") == null ? null : getNumberValue(bundle, "age").intValue();
        this.month = getNumberValue(bundle, "month") == null ? null : getNumberValue(bundle, "month").intValue();
        this.status = bundle.containsKey("status") ? new Status(bundle.getBundle("status")) : null;
        this.position = bundle.containsKey("position") ? new Position(bundle.getBundle("position")) : null;
        this.birthYear = bundle.containsKey("birthYear") ? new BirthYear(bundle.getBundle("birthYear")) : null;
        this.addressList = bundle.containsKey("addressList") ? getList(bundle.getParcelableArray("addressList"), Address.class) : new ArrayList<Address>();
        this.siblingsNames = bundle.containsKey("siblingsNames") ? getList(bundle.getStringArray("siblingsNames"), String.class) : new ArrayList<String>();
        this.siblingsAges = bundle.containsKey("siblingsAges") ? getList(bundle.get("siblingsAges"), Integer.class) : new ArrayList<Integer>();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public Integer getAge() {
        return age;
    }

    /**
     * Month hired
     *
     * @return Integer
     */
    @NonNull
    public Integer getMonth() {
        return month;
    }

    /**
     * Membership status
     *
     * @return Status
     */
    @Nullable
    public Status getStatus() {
        return status;
    }

    @Nullable
    public Position getPosition() {
        return position;
    }

    @Nullable
    public BirthYear getBirthYear() {
        return birthYear;
    }

    @NonNull
    public List<Address> getAddressList() {
        return addressList;
    }

    @NonNull
    public List<String> getSiblingsNames() {
        return siblingsNames;
    }

    @NonNull
    public List<Integer> getSiblingsAges() {
        return siblingsAges;
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
        bundle.putString("name", name);
        if (age != null) {
            bundle.putInt("age", age);
        }
        bundle.putInt("month", month);
        if (status != null) {
            bundle.putParcelable("status", status.toBundle());
        }
        if (position != null) {
            bundle.putParcelable("position", position.toBundle());
        }
        if (birthYear != null) {
            bundle.putParcelable("birthYear", birthYear.toBundle());
        }

        if (addressList != null && !addressList.isEmpty()) {
            bundle.putParcelableArray("addressList", bridgeablesToBundleArray(addressList));
        }

        if (siblingsNames != null && !siblingsNames.isEmpty()) {
            bundle.putStringArray("siblingsNames", siblingsNames.toArray(new String[]{}));
        }

        if (siblingsAges != null && !siblingsAges.isEmpty()) {
            bundle.putIntArray("siblingsAges", toIntArray(getSiblingsAges()));
        }

        return bundle;
    }

    public static class Builder {
        private final String name;
        private Integer age;
        private final Integer month;
        private Status status;
        private Position position;
        private BirthYear birthYear;
        private List<Address> addressList;
        private List<String> siblingsNames;
        private List<Integer> siblingsAges;

        public Builder(@NonNull String name, @NonNull Integer month) {
            this.name = name;
            this.month = month;
        }

        @NonNull
        public Builder age(@Nullable Integer age) {
            this.age = age;
            return this;
        }

        @NonNull
        public Builder status(@Nullable Status status) {
            this.status = status;
            return this;
        }

        @NonNull
        public Builder position(@Nullable Position position) {
            this.position = position;
            return this;
        }

        @NonNull
        public Builder birthYear(@Nullable BirthYear birthYear) {
            this.birthYear = birthYear;
            return this;
        }

        public Builder addresses(@Nullable List<Address> addressList) {
            this.addressList = addressList;
            return this;
        }

        public Builder siblingsNames(@Nullable List<String> siblingsNames) {
            this.siblingsNames = siblingsNames;
            return this;
        }

        public Builder siblingsAges(@Nullable List<Integer> siblingsAges) {
            this.siblingsAges = siblingsAges;
            return this;
        }


        @NonNull
        public Person build() {
            return new Person(this);
        }
    }
}