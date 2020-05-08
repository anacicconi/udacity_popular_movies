package com.cicconi.popularmovies.network;

import com.cicconi.popularmovies.model.MoviesResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDBApi {

    String API_KEY_PARAM = "api_key";
    String PAGINATION_PARAM = "page";

    @GET("movie/popular")
    Single<MoviesResponse> getPopularMovies(@Query(API_KEY_PARAM) String apiKey, @Query(PAGINATION_PARAM) String page);

    @GET("movie/top_rated")
    Single<MoviesResponse> getTopRatedMovies(@Query(API_KEY_PARAM) String apiKey, @Query(PAGINATION_PARAM) String page);
}
