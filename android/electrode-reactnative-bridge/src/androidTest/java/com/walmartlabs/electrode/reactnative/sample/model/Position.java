package com.walmartlabs.electrode.reactnative.sample.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Position implements Parcelable {

    private static final String KEY_BUNDLE_POSITION = "position";

    @Nullable
    public static Position fromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        Parcelable parcelable = bundle.getParcelable(KEY_BUNDLE_POSITION);
        if (parcelable instanceof Position) {
            return (Position) parcelable;
        } else {
            return null;
        }
    }

    private final Integer lat;
    private final Integer lng;

    private Position(Builder builder) {
        this.lat = builder.lat;
        this.lng = builder.lng;
    }

    private Position(Parcel in) {
        lat = in.readInt();
        lng = in.readInt();
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    @Nullable
    public Integer getLat() {
        return lat;
    }

    @Nullable
    public Integer getLng() {
        return lng;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lat);
        dest.writeInt(lng);
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BUNDLE_POSITION, this);
        return bundle;
    }

    public static class Builder {
        private Integer lat;
        private Integer lng;

        public Builder() {
        }

        @NonNull
        public Builder lat(@Nullable Integer lat) {
            this.lat = lat;
            return this;
        }

        @NonNull
        public Builder lng(@Nullable Integer lng) {
            this.lng = lng;
            return this;
        }

        @NonNull
        public Position build() {
            return new Position(this);
        }
    }
}
