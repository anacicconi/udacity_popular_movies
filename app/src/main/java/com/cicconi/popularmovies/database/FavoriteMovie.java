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
    @ColumnInfo(name = "vote_average")
    private Double voteAverage;
    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @Ignore
    public FavoriteMovie(int apiId, String originalTitle, String overview, String releaseDate, Double voteAverage, String posterPath) {
        this.apiId = apiId;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
    }

    FavoriteMovie(int id, int apiId, String originalTitle, String overview, String releaseDate, Double voteAverage,
        String posterPath) {
        this.id = id;
        this.apiId = apiId;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public int getApiId() {
        return apiId;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
