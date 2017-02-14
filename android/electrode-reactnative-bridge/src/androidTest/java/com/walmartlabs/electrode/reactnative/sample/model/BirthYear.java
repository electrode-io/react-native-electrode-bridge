package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BirthYear implements Parcelable {

    private static final String KEY_BUNDLE_BIRTHYEAR = "birthYear";

    @Nullable
    public static BirthYear fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        Parcelable parcelable = bundle.getParcelable(KEY_BUNDLE_BIRTHYEAR);
        if (parcelable instanceof BirthYear) {
            return (BirthYear) parcelable;
        } else {
            return null;
        }
    }

    private final Integer month;
    private final Integer year;

    private BirthYear(Builder builder) {
        this.month = builder.month;
        this.year = builder.year;
    }

    private BirthYear(Parcel in) {
        month = in.readInt();
        year = in.readInt();
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
        dest.writeInt(month);
        dest.writeInt(year);
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BUNDLE_BIRTHYEAR, this);
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
