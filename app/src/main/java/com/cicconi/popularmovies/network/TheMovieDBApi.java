package com.cicconi.popularmovies.network;

import com.cicconi.popularmovies.model.MovieResponse;
import com.cicconi.popularmovies.model.ReviewResponse;
import com.cicconi.popularmovies.model.VideoResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDBApi {

    String API_KEY_PARAM = "api_key";
    String PAGINATION_PARAM = "page";
    String ID = "id";

    @GET("movie/popular")
    Observable<MovieResponse> getPopularMovies(@Query(API_KEY_PARAM) String apiKey, @Query(PAGINATION_PARAM) String page);

    @GET("movie/top_rated")
    Observable<MovieResponse> getTopRatedMovies(@Query(API_KEY_PARAM) String apiKey, @Query(PAGINATION_PARAM) String page);

    @GET("movie/{id}/videos")
    Observable<VideoResponse> getMovieVideos(@Path(ID) int id, @Query(API_KEY_PARAM) String apiKey);

    @GET("movie/{id}/reviews")
    Observable<ReviewResponse> getMovieReviews(@Path(ID) int id, @Query(API_KEY_PARAM) String apiKey);
}
