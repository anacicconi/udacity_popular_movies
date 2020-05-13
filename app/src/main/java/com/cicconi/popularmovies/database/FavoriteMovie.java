package com.cicconi.popularmovies.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movie")
public class FavoriteMovie {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int apiId;
    @ColumnInfo(name = "title")
    private String originalTitle;
    private String overview;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    private Double popularity;
    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @Ignore
    public FavoriteMovie(int apiId, String originalTitle, String overview, String releaseDate, Double popularity, String posterPath) {
        this.apiId = apiId;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.posterPath = posterPath;
    }

    public FavoriteMovie(int id, int apiId, String originalTitle, String overview, String releaseDate, Double popularity, String posterPath) {
        this.id = id;
        this.apiId = apiId;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
