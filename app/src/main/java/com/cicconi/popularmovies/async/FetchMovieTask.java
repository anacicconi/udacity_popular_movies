package com.cicconi.popularmovies.async;

import android.os.AsyncTask;
import com.cicconi.popularmovies.MainActivity;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.utils.MovieJsonUtils;
import com.cicconi.popularmovies.utils.NetworkUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchMovieTask extends AsyncTask<Integer, Void, List<Movie>> {

    private FetchMovieStart fetchMovieStart;
    private FetchMovieEnd fetchMovieEnd;

    public FetchMovieTask(MainActivity activity) {
        fetchMovieStart = activity;
        fetchMovieEnd = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        fetchMovieStart.loadStart();
    }

    @Override
    protected List<Movie> doInBackground(Integer... params) {
        List<Movie> emptyMovieList = new ArrayList<>();
        if(!isMobileOnline()) {
            return emptyMovieList;
        }

        int sortType = params[0];
        int pagination = params[1];
        URL weatherRequestUrl = NetworkUtils.buildUrl(sortType, String.valueOf(pagination));

        try {
            String moviesResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            return MovieJsonUtils.getMoviesFromJson(moviesResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return emptyMovieList;
        }
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        fetchMovieEnd.loadFinish(movies);
    }

    boolean isMobileOnline() {
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
    }

    public interface FetchMovieStart {
        void loadStart();
    }

    public interface FetchMovieEnd {
        void loadFinish(List<Movie> movies);
    }
}
