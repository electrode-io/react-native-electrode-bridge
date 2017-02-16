package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Person implements Parcelable {
    private static final String KEY_BUNDLE_ID = "className";
    private static final String VALUE_BUNDLE_ID = Person.class.getSimpleName();

    @Nullable
    public static Person fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (!bundle.containsKey(KEY_BUNDLE_ID)
                || !(VALUE_BUNDLE_ID).equals(bundle.getString(KEY_BUNDLE_ID))) {
            return null;
        }
        //Validate to make sure all required fields are available
        if (!bundle.containsKey("name")
                || !bundle.containsKey("month")) {
            return null;
        }
        return new Builder(bundle.getString("name"), (int) bundle.getDouble("month"))
                .age(bundle.containsKey("age") ? bundle.getInt("age") : null)
                .status(bundle.containsKey("status") ? Status.fromBundle(bundle.getBundle("status")) : null)
                .position(bundle.containsKey("position") ? Position.fromBundle(bundle.getBundle("position")) : null)
                .birthYear(bundle.containsKey("birthYear") ? BirthYear.fromBundle(bundle.getBundle("birthYear")) : null)
                .build();
    }

    private final String name;
    private final Number month;
    private final Integer age;
    private final Status status;
    private final Position position;
    private final BirthYear birthYear;

    private Person(Builder builder) {
        this.name = builder.name;
        this.month = builder.month;
        this.age = builder.age;
        this.status = builder.status;
        this.position = builder.position;
        this.birthYear = builder.birthYear;
    }

    protected Person(Parcel in) {
        Bundle bundle = in.readBundle();
        this.name = bundle.getString("name");
        this.month = bundle.getDouble("month");
        this.age = bundle.containsKey("age") ? bundle.getInt("age") : null;
        this.status = bundle.containsKey("status") ? Status.fromBundle(bundle.getBundle("status")) : null;
        this.position = bundle.containsKey("position") ? Position.fromBundle(bundle.getBundle("position")) : null;
        this.birthYear = bundle.containsKey("birthYear") ? BirthYear.fromBundle(bundle.getBundle("birthYear")) : null;
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

    /**
     * Month hired
     *
     * @return Integer
     */
    @NonNull
    public Number getMonth() {
        return month;
    }

    @Nullable
    public Integer getAge() {
        return age;
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
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("month", month.intValue());
        if (age != null) {
            bundle.putInt("age", age);
        }
        if (status != null) {
            bundle.putParcelable("status", status.toBundle());
        }
        if (position != null) {
            bundle.putParcelable("position", position.toBundle());
        }
        if (birthYear != null) {
            bundle.putParcelable("birthYear", birthYear.toBundle());
        }
        bundle.putString(KEY_BUNDLE_ID, VALUE_BUNDLE_ID);
        return bundle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(toBundle());
    }

    public static class Builder {
        private final String name;
        private final Integer month;
        private Integer age;
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