package com.cicconi.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Completable;
import java.util.List;

@Dao
public interface FavoriteMovieDAO {

    @Query("SELECT * FROM favorite_movie ORDER BY release_date")
    LiveData<List<FavoriteMovie>> loadAllFavoriteMovies();

    @Insert
    Completable insertFavoriteMovie(FavoriteMovie favoriteMovie);
}
