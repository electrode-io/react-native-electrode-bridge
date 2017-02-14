package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Person implements Parcelable {

    private static final String KEY_BUNDLE_PERSON = "person";

    @Nullable
    public static Person fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        Parcelable parcelable = bundle.getParcelable(KEY_BUNDLE_PERSON);
        if (parcelable instanceof Person) {
            return (Person) parcelable;
        } else {
            return null;
        }
    }

    private final String name;
    private final Integer age;
    private final Integer month;
    private final Status status;
    private final Position position;
    private final BirthYear birthYear;

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.month = builder.month;
        this.status = builder.status;
        this.position = builder.position;
        this.birthYear = builder.birthYear;
    }

    private Person(Parcel in) {
        name = in.readString();
        age = in.readInt();
        month = in.readInt();
        status = in.readParcelable(Status.class.getClassLoader());
        position = in.readParcelable(Position.class.getClassLoader());
        birthYear = in.readParcelable(BirthYear.class.getClassLoader());
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
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeInt(month);
        dest.writeParcelable(status, flags);
        dest.writeParcelable(position, flags);
        dest.writeParcelable(birthYear, flags);
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BUNDLE_PERSON, this);
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
