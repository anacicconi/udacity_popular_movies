package com.cicconi.popularmovies.async;

import android.util.Log;
import com.cicconi.popularmovies.model.OldMovie;
import com.cicconi.popularmovies.utils.MovieJsonUtils;
import com.cicconi.popularmovies.utils.NetworkUtils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;

public class FetchMovieTask {

    private static final String TAG = FetchMovieTask.class.getSimpleName();

    public static final int SORT_RATING = 101;
    public static final int SORT_POPULAR = 100;

    /**
     * Calls isOnline and pass the result to requestUrl to get a list of movies.
     *
     * @param sortType popular or top rated
     * @param pagination api page
     * @return Observable<List<OldMovie>>
     */
    public Observable<List<OldMovie>> getMovies(Integer sortType, String pagination) {
        String endpoint;

        if(sortType == SORT_RATING) {
            endpoint = NetworkUtils.TOP_RATED_MOVIES_URL;
        } else {
            endpoint = NetworkUtils.POPULAR_MOVIES_URL;
        }

        return isMobileOnline()
            .subscribeOn(Schedulers.io())
            .switchMap(isOnline -> requestUrl(isOnline, endpoint, pagination))
            .defaultIfEmpty("") // if empty the default will be "" because the ui needs a response to know how to handle this case
            .map(MovieJsonUtils::getMoviesFromJson);
    }

    /**
     * Calls isOnline and pass the result to requestUrl to get a list of videos for a movie.
     *
     * @param id movie id
     * @return Observable<List<OldMovie>>
     */
    public Observable<List<OldMovie>> getVideosById(String id) {

        return isMobileOnline()
            .subscribeOn(Schedulers.io())
            .switchMap(isOnline -> requestUrl(isOnline, NetworkUtils.VIDEO_MOVIES_URL, null))
            .defaultIfEmpty("") // if empty the default will be "" because the ui needs a response to know how to handle this case
            .map(MovieJsonUtils::getMoviesFromJson);
    }

    /**
     * Calls isOnline and pass the result to requestUrl to get a list of reviews for a movie.
     *
     * @param id movie id
     * @return Observable<List<OldMovie>>
     */
    public Observable<List<OldMovie>> getReviewsById(String id) {

        return isMobileOnline()
            .subscribeOn(Schedulers.io())
            .switchMap(isOnline -> requestUrl(isOnline, NetworkUtils.VIDEO_REVIEWS_URL, null))
            .defaultIfEmpty("") // if empty the default will be "" because the ui needs a response to know how to handle this case
            .map(MovieJsonUtils::getMoviesFromJson);
    }

    /**
     * Requests API url.
     *
     * @param isOnline if internet is available
     * @param endpoint endpoint to request
     * @param pagination api page
     * @return Observable<String>
     */
    private Observable<String> requestUrl(Boolean isOnline, String endpoint, String pagination) {
        Log.i(TAG, String.format("Thread requestUrl: %s", Thread.currentThread().getName()));

        Observable<String> emptyMovieObservable = Observable.empty();

        if(!isOnline) {
            return emptyMovieObservable;
        }

        URL weatherRequestUrl = NetworkUtils.buildUrl(endpoint, pagination);

        try {
            return NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
        } catch (Exception e) {
            Log.i(TAG, String.format("Error: %s", e.getMessage()));

            return emptyMovieObservable;
        }
    }

    /**
     * Calls google DNS to check if internet is available.
     *
     * @return Observable<Boolean>
     */
    private Observable<Boolean> isMobileOnline() {

        return Observable.fromCallable(() -> {
            Log.i(TAG, String.format("Thread isMobileOnline: %s", Thread.currentThread().getName()));

            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                // check google dns
                SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(socketAddress, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) {
                return false;
            }
        });
    }
}
