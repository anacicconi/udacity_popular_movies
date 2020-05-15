package com.cicconi.popularmovies.viewmodel;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.model.Review;
import com.cicconi.popularmovies.model.Video;
import com.cicconi.popularmovies.repository.MovieRepository;
import io.reactivex.Completable;
import java.util.List;

public class DetailsViewModel extends ViewModel {

    private static final String TAG = DetailsViewModel.class.getSimpleName();

    private MovieRepository movieRepository;

    private LiveData<Boolean> isFavoriteMovie;

    private Movie movie;

    DetailsViewModel(@NonNull Context context, Movie movie) {
        movieRepository = new MovieRepository(context);
        this.movie = movie;

        // if the movie came from the api, the boolean is not set
        // so we check if the id of the movie is in the database
        if(movie.getFavorite() != null) {
            isFavoriteMovie = new MutableLiveData<>(movie.getFavorite());
        } else {
            isFavoriteMovie = checkIsFavoriteMovie();
        }
    }

    public LiveData<List<Video>> getVideos() {
        Log.i(TAG, String.format("Getting videos of movie %d", movie.getId()));

        return movieRepository.getVideosById(movie.getId());
    }

    public LiveData<List<Review>> getReviews() {
        Log.i(TAG, String.format("Getting reviews of movie %d", movie.getId()));

        return movieRepository.getReviewsById(movie.getId());
    }

    public LiveData<Boolean> getIsFavoriteMovie() {
        return isFavoriteMovie;
    }

    // Checking if the id is in the database. If it is we return a LiveData "false" to isFavoriteMovie.
    private LiveData<Boolean> checkIsFavoriteMovie() {
        return Transformations.map(movieRepository.getFavoriteMovieByApiId(movie.getId()), (data) ->
            data != null
        );
    }

    // Adding movie to database
    public Completable onMovieAddedToFavorites() {
        return movieRepository.addFavoriteMovie(movie.toFavoriteMovie());
    }

    // Removing movie from database
    public Completable onMovieRemovedFromFavorites() {
        return movieRepository.deleteFavoriteMovie(movie.getId());
    }
}
