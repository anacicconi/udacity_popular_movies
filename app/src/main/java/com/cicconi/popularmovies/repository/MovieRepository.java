package com.cicconi.popularmovies.repository;

import android.util.Log;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.network.RetrofitBuilder;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.model.MoviesResponse;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();

    //TODO: add your api key
    private static final String API_KEY = "";

    public Observable<List<Movie>> getMovies(String page, int category) {
        if(category == Constants.SORT_POPULAR) {
            return getPopularMovies(page);
        }

        if(category == Constants.SORT_RATING) {
            return getTopRatedMovies(page);
        }

        // always return an empty object in case of error so the view knows that it has to display the error message
        return Observable.just(getEmptyMoviesList());
    }

    private Observable<List<Movie>> getPopularMovies(String page) {
        return RetrofitBuilder.getClient().getPopularMovies(API_KEY, page)
            .subscribeOn(Schedulers.io())
            .doOnNext(i -> Log.d(TAG, String.format("Thread getPopularMovies: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> getEmptyMoviesResponse())
            .map(moviesResponse -> {
                if(moviesResponse.getResults() != null) {
                    return moviesResponse.getResults();
                }

                return getEmptyMoviesList();
            });
    }

    private Observable<List<Movie>> getTopRatedMovies(String page) {
        return RetrofitBuilder.getClient().getTopRatedMovies(API_KEY, page)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(i -> Log.d(TAG, String.format("Thread getTopRatedMovies: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> getEmptyMoviesResponse())
            .map(moviesResponse -> {
                if(moviesResponse.getResults() != null) {
                    return moviesResponse.getResults();
                }

                return getEmptyMoviesList();
            });
    }

    private MoviesResponse getEmptyMoviesResponse() {
        return new MoviesResponse();
    }

    private List<Movie> getEmptyMoviesList() {
        return new ArrayList<>();
    }
}
