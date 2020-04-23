package com.cicconi.popularmovies.async;

import android.util.Log;
import com.cicconi.popularmovies.model.Movie;
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

    /**
     * Calls isOnline and pass the result to requestUrl.
     *
     * @param sortType popular or top rated
     * @param pagination api page
     * @return Observable<List<Movie>>
     */
    public Observable<List<Movie>> loadData(Integer sortType, String pagination) {

        return isMobileOnline()
            .subscribeOn(Schedulers.io())
            .switchMap(isOnline -> requestUrl(isOnline, sortType, pagination))
            .defaultIfEmpty("") // if empty the default will be "" because the ui needs a response to know how to handle this case
            .map(MovieJsonUtils::getMoviesFromJson);
    }

    /**
     * Requests API url.
     *
     * @param isOnline if internet is available
     * @param sortType popular or top rated
     * @param pagination api page
     * @return Observable<String>
     */
    private Observable<String> requestUrl(Boolean isOnline, int sortType, String pagination) {
        Log.i(TAG, String.format("Thread requestUrl: %s", Thread.currentThread().getName()));

        Observable<String> emptyMovieObservable = Observable.empty();

        if(!isOnline) {
            return emptyMovieObservable;
        }

        URL weatherRequestUrl = NetworkUtils.buildUrl(sortType, pagination);

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
