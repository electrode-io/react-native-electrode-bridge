package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Status implements Parcelable {

    private static final String KEY_BUNDLE_STATUS = "status";

    @Nullable
    public static Status fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        Parcelable parcelable = bundle.getParcelable(KEY_BUNDLE_STATUS);
        if (parcelable instanceof Status) {
            return (Status) parcelable;
        } else {
            return null;
        }
    }

    private final Boolean log;
    private final Boolean member;

    private Status(Builder builder) {
        this.log = builder.log;
        this.member = builder.member;
    }

    private Status(Parcel in) {
        log = in.readInt() != 0;
        member = in.readInt() != 0;
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
        dest.writeInt(log ? 1 : 0);
        dest.writeInt(member ? 1 : 0);
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BUNDLE_STATUS, this);
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
