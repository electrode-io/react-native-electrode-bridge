package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;

import static com.walmartlabs.electrode.reactnative.bridge.util.BridgeArguments.getNumberValue;

public class Person implements Parcelable, Bridgeable {

    private String name;
    private Integer age;
    private Integer month;
    private Status status;
    private Position position;
    private BirthYear birthYear;

    private Person() {
    }

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.month = builder.month;
        this.status = builder.status;
        this.position = builder.position;
        this.birthYear = builder.birthYear;
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
        if(age != null) {
            bundle.putInt("age", age);
        }
        bundle.putInt("month", month);
        if(status != null) {
            bundle.putParcelable("status", status.toBundle());
        }
        if(position != null) {
            bundle.putParcelable("position", position.toBundle());
        }
        if(birthYear != null) {
            bundle.putParcelable("birthYear", birthYear.toBundle());
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

        @NonNull
        public Person build() {
            return new Person(this);
        }
    }
}