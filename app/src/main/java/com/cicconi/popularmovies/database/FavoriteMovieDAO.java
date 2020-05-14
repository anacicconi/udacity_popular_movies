package com.cicconi.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import java.util.List;

@Dao
public interface FavoriteMovieDAO {

    @Query("SELECT * FROM favorite_movie ORDER BY release_date")
    LiveData<List<FavoriteMovie>> loadAllFavoriteMovies();

    @Query("SELECT apiId FROM favorite_movie WHERE apiId = :apiId")
    LiveData<Integer> loadFavoriteMovieByApiId(int apiId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFavoriteMovie(FavoriteMovie favoriteMovie);

    @Query("DELETE FROM favorite_movie WHERE apiId = :apiId")
    Completable deleteFavoriteMovie(int apiId);
}
