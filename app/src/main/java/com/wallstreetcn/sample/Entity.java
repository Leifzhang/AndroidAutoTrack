package com.wallstreetcn.sample;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Entity implements Parcelable {
    private String test = "test";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.test);
    }

    public Entity() {
    }

    @NonNull
    @Override
    public String toString() {
        return "Entity";
    }

    protected Entity(Parcel in) {
        this.test = in.readString();
    }

    public static final Creator<Entity> CREATOR = new Creator<Entity>() {
        @Override
        public Entity createFromParcel(Parcel source) {
            return new Entity(source);
        }

        @Override
        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
}

