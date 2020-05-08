package com.cicconi.popularmovies.repository;

import android.util.Log;
import com.cicconi.popularmovies.network.RetrofitBuilder;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.model.MoviesResponse;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();

    //TODO: add your api key
    private static final String API_KEY = "";

    public Observable<List<Movie>> getPopularMovies(String page) {
        MoviesResponse emptyMoviesResponse = new MoviesResponse();
        List<Movie> emptyMovieList = new ArrayList<>();

        return RetrofitBuilder.getClient().getPopularMovies(API_KEY, page)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(i -> Log.d(TAG, String.format("Thread getPopularMovies: %s", Thread.currentThread().getName())))
            .onErrorReturn(e -> emptyMoviesResponse)
            .map(moviesResponse -> {
                if(moviesResponse.getResults() != null) {
                    return moviesResponse.getResults();
                }

                return emptyMovieList;
            });
    }
}
