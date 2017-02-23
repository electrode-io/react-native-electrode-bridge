package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.walmartlabs.electrode.reactnative.bridge.Bridgeable;

public class BirthYear implements Parcelable,Bridgeable {

    private static final String KEY_BUNDLE_ID = "className";
    private static final String VALUE_BUNDLE_ID = BirthYear.class.getSimpleName();

    @Nullable
    public static BirthYear fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        if (!bundle.containsKey(KEY_BUNDLE_ID)
                || !(VALUE_BUNDLE_ID).equals(bundle.getString(KEY_BUNDLE_ID))) {
            return null;
        }

        //Validate to make sure all required fields are available
        if (!bundle.containsKey("month")
                || !bundle.containsKey("year")) {
            return null;
        }

        return new Builder(bundle.getInt("month"), bundle.getInt("year")).build();
    }

    private final Integer month;
    private final Integer year;

    private BirthYear(Builder builder) {
        this.month = builder.month;
        this.year = builder.year;
    }

    private BirthYear(Parcel in) {
        this(in.readBundle());
    }

    public BirthYear(Bundle bundle) {
        month = bundle.getInt("month");
        year = bundle.getInt("year");
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
        bundle.putString(KEY_BUNDLE_ID, VALUE_BUNDLE_ID);
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
