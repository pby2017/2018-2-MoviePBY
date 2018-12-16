package com.pby.user.moviepby;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public final class Movie {
    @Nullable
    private String link;


    @Nullable
    private String title;

    @Nullable
    private Bitmap image;

    @Nullable
    private String userRating;

    @Nullable
    private String pubDate;

    @Nullable
    private String director;

    @Nullable
    private String actor;

    public Movie() {
    }

    public Movie(@Nullable String link, @Nullable Bitmap image,
                 @Nullable String title, @Nullable String userRating,
                 @Nullable String pubDate, @Nullable String director, @Nullable String actor) {
        this.link = link;
        this.title = title;
        this.image = image;
        this.userRating = userRating;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
    }

    @Nullable
    public String getmLink() {
        return link;
    }

    @Nullable
    public String getmTitle() {
        return title;
    }

    @Nullable
    public Bitmap getmImage() {
        return image;
    }

    @Nullable
    public String getmUserRating() {
        return userRating;
    }

    @Nullable
    public String getmPubDate() {
        return pubDate;
    }

    @Nullable
    public String getmDirector() {
        return director;
    }

    @Nullable
    public String getmActor() {
        return actor;
    }
}
