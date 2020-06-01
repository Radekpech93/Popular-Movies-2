package com.example.wizi.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String mTitle;
    private String mPoster;
    private String mOverview;
    private String mAverageVote;
    private String mReleaseDate;
    private String mId;


    public Movie() {
    }

    public Movie(String title, String poster, String overview, String averageVote, String releaseDate, String id) {
        mTitle = title;
        mPoster = poster;
        mOverview = overview;
        mAverageVote = averageVote;
        mReleaseDate = releaseDate;
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String poster) {
        mPoster = poster;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getAverageVote() {
        return mAverageVote;
    }

    public void setAverageVote(String averageVote) {
        mAverageVote = averageVote;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mTitle);
        out.writeString(mPoster);
        out.writeString(mOverview);
        out.writeString(mAverageVote);
        out.writeString(mReleaseDate);
        out.writeString(mId);
    }

    public Movie(Parcel in) {
        mTitle = in.readString();
        mPoster = in.readString();
        mOverview = in.readString();
        mAverageVote = in.readString();
        mReleaseDate = in.readString();
        mId = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };
}
