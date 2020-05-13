package com.cicconi.popularmovies.repository;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.database.AppDatabase;
import com.cicconi.popularmovies.database.FavoriteMovie;
import com.cicconi.popularmovies.model.Review;
import com.cicconi.popularmovies.model.ReviewResponse;
import com.cicconi.popularmovies.model.Video;
import com.cicconi.popularmovies.model.VideoResponse;
import com.cicconi.popularmovies.network.RetrofitBuilder;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.model.MovieResponse;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();

    private static AppDatabase mDb;

    //TODO: add your api key
    private static final String API_KEY = "";

    public MovieRepository(Context context) {
        mDb = AppDatabase.getInstance(context);
    }

    public Observable<List<Movie>> getMovies(String page, int category) {
        if(category == Constants.SORT_POPULAR) {
            return getPopularMovies(page);
        }

        if(category == Constants.SORT_RATING) {
            return getTopRatedMovies(page);
        }

        // always return an empty object in case of error so the view knows that it has to display the error message
        return Observable.just(getEmptyMovieList());
    }

    private Observable<List<Movie>> getPopularMovies(String page) {
        return RetrofitBuilder.getClient().getPopularMovies(API_KEY, page)
            .subscribeOn(Schedulers.io())
            .doOnNext(i -> Log.d(TAG, String.format("Thread getPopularMovies: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> getEmptyMovieResponse())
            .map(moviesResponse -> {
                if(moviesResponse.getResults() != null) {
                    return moviesResponse.getResults();
                }

                return getEmptyMovieList();
            });
    }

    private Observable<List<Movie>> getTopRatedMovies(String page) {
        return RetrofitBuilder.getClient().getTopRatedMovies(API_KEY, page)
            .subscribeOn(Schedulers.io())
            .doOnNext(i -> Log.d(TAG, String.format("Thread getTopRatedMovies: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> getEmptyMovieResponse())
            .map(moviesResponse -> {
                if(moviesResponse.getResults() != null) {
                    return moviesResponse.getResults();
                }

                return getEmptyMovieList();
            });
    }

    public LiveData<List<FavoriteMovie>> getFavoriteMovies() {
        return mDb.favoriteMovieDAO().loadAllFavoriteMovies();
    }

    public Observable<List<Video>> getVideosById(int movieId) {
        return RetrofitBuilder.getClient().getMovieVideos(movieId, API_KEY)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(i -> Log.d(TAG, String.format("Thread getVideosById: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> getEmptyVideoResponse())
            .map(videoResponse -> {
                if(videoResponse.getResults() != null) {
                    return videoResponse.getResults();
                }

                return getEmptyVideoList();
            });
    }

    public Observable<List<Review>> getReviewsById(int movieId) {
        return RetrofitBuilder.getClient().getMovieReviews(movieId, API_KEY)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(i -> Log.d(TAG, String.format("Thread getReviewsById: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> getEmptyReviewResponse())
            .map(reviewResponse -> {
                if(reviewResponse.getResults() != null) {
                    return reviewResponse.getResults();
                }

                return getEmptyReviewList();
            });
    }

    private MovieResponse getEmptyMovieResponse() {
        return new MovieResponse();
    }

    private List<Movie> getEmptyMovieList() {
        return new ArrayList<>();
    }

    private VideoResponse getEmptyVideoResponse() {
        return new VideoResponse();
    }

    private List<Video> getEmptyVideoList() {
        return new ArrayList<>();
    }

    private ReviewResponse getEmptyReviewResponse() {
        return new ReviewResponse();
    }

    private List<Review> getEmptyReviewList() {
        return new ArrayList<>();
    }
}
