package com.cicconi.popularmovies.model;

import java.io.Serializable;

public class OldMovie implements Serializable {
    private final String title;
    private final String image;
    private final String synopsis;
    private final String rating;
    private final String releaseDate;
    private final int page;

    public OldMovie(String title, String image, String synopsis, String rating, String releaseDate, int page) {
        if(!title.isEmpty()) this.title = title; else this.title = "Unknown";
        if(!synopsis.isEmpty()) this.synopsis = synopsis; else this.synopsis = "Unknown";
        if(!releaseDate.isEmpty()) this.releaseDate = releaseDate; else this.releaseDate = "Unknown";
        this.image = image;
        this.rating = rating;
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getPage() {
        return page;
    }
}
