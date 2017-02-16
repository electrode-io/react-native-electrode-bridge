package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Status implements Parcelable {

    private static final String KEY_BUNDLE_ID = "className";
    private static final String VALUE_BUNDLE_ID = Person.class.getSimpleName();

    @Nullable
    public static Status fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        if (!bundle.containsKey(KEY_BUNDLE_ID)
                || !(VALUE_BUNDLE_ID).equals(bundle.getString(KEY_BUNDLE_ID))) {
            return null;
        }

        //Validate to make sure all required fields are available
        if (!bundle.containsKey("member")) {
            return null;
        }

        return new Builder(bundle.getBoolean("member")).log(bundle.getBoolean("log")).build();
    }

    private final Boolean member;
    private final Boolean log;

    private Status(Builder builder) {
        this.member = builder.member;
        this.log = builder.log;
    }

    private Status(Parcel in) {
        Bundle bundle = in.readBundle();
        member = bundle.getBoolean("member");
        log = bundle.getBoolean("log");
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

    /**
     * Log ???
     *
     * @return Boolean
     */
    @Nullable
    public Boolean getLog() {
        return log;
    }

    /**
     * Is the user a Sam&#39;s club member
     *
     * @return Boolean
     */
    @NonNull
    public Boolean getMember() {
        return member;
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
        bundle.putBoolean("member", member);
        if (log != null) {
            bundle.putBoolean("log", log);
        }
        return bundle;
    }

    public static class Builder {
        private Boolean log;
        private final Boolean member;

        public Builder(@NonNull Boolean member) {
            this.member = member;
        }

        @NonNull
        public Builder log(@Nullable Boolean log) {
            this.log = log;
            return this;
        }

        @NonNull
        public Status build() {
            return new Status(this);
        }
    }
}
