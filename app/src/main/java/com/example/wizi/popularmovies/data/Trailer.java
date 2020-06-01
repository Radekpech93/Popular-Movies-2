package com.example.wizi.popularmovies.data;

//This is a simple class that basically only contains a String - it only exists for consistency and code readability

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    private String mKey;

    public Trailer() {
    }

    public Trailer(String key) { mKey = key; }

    protected Trailer(Parcel in) {
        mKey = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getKey() { return mKey; }

    public void setKey(String key) { mKey = key; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mKey);
    }

}
