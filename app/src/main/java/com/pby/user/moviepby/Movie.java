package com.pby.user.moviepby;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public final class Movie {
    @Nullable
    private String mLink;

    @Nullable
    private String mTitle;

    @Nullable
    private Bitmap mImage;

    @Nullable
    private String mUserRating;

    @Nullable
    private String mPubDate;

    @Nullable
    private String mDirector;

    @Nullable
    private String mActor;

    public Movie() {
    }

    public Movie(@Nullable String link, @Nullable Bitmap image,
                 @Nullable String title, @Nullable String userRating,
                 @Nullable String pubDate, @Nullable String director, @Nullable String actor) {
        mLink = link;
        mTitle = title;
        mImage = image;
        mUserRating = userRating;
        mPubDate = pubDate;
        mDirector = director;
        mActor = actor;
    }

    @Nullable
    public String getmLink() {
        return mLink;
    }

    @Nullable
    public String getmTitle() {
        return mTitle;
    }

    @Nullable
    public Bitmap getmImage() {
        return mImage;
    }

    @Nullable
    public String getmUserRating() {
        return mUserRating;
    }

    @Nullable
    public String getmPubDate() {
        return mPubDate;
    }

    @Nullable
    public String getmDirector() {
        return mDirector;
    }

    @Nullable
    public String getmActor() {
        return mActor;
    }
}
