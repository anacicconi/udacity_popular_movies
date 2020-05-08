package com.cicconi.popularmovies.utils;

import android.net.Uri;
import android.util.Log;
import io.reactivex.rxjava3.core.Observable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * DEPRECATED since Retrofit migration
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular";
    public static final String TOP_RATED_MOVIES_URL = "https://api.themoviedb.org/3/movie/top_rated";
    public static final String VIDEO_MOVIES_URL = "https://api.themoviedb.org/3/movie/{id}/videos";
    public static final String VIDEO_REVIEWS_URL = "https://api.themoviedb.org/3/movie/{id}/reviews";
    private final static String API_KEY_PARAM = "api_key";
    private final static String PAGINATION_PARAM = "page";
    //TODO: add your api key
    private static final String API_KEY = "";

    /**
     * @return The URL to use to query the movies api.
     * @param endpoint endpoint to request
     * @param pagination the page to request
     */
    public static URL buildUrl(String endpoint, String pagination) {
        Log.i(TAG, String.format("Thread buildUrl: %s", Thread.currentThread().getName()));

        Uri builtUri = Uri.parse(endpoint).buildUpon()
            .appendQueryParameter(API_KEY_PARAM, API_KEY)
            .appendQueryParameter(PAGINATION_PARAM, pagination)
            .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static Observable<String> getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        String emptyResult = "";

        // creates an observable from the result of this method
        return Observable.fromCallable(() -> {
            Log.i(TAG, String.format("Thread getResponseFromHttpUrl: %s", Thread.currentThread().getName()));

            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return emptyResult;
                }
            } catch(Exception e) {
                System.out.println(e.getMessage());
                return emptyResult;
            } finally {
                urlConnection.disconnect();
            }
        });
    }
}