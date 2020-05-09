package com.cicconi.popularmovies.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.repository.MovieRepository;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private MovieRepository movieRepository;

    // Had to use a MediatorLiveData because the LiveDataReactiveStreams does not accept a MutableLiveData
    private MediatorLiveData<List<Movie>> movies = new MediatorLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        movieRepository = new MovieRepository();

        Log.d(TAG, "Initializing movies list");

        movies.addSource(
            LiveDataReactiveStreams.fromPublisher(
                movieRepository.getMovies(String.valueOf(1), Constants.SORT_POPULAR)
                    .toFlowable(BackpressureStrategy.BUFFER)
            ), value -> movies.setValue(value));
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void updateMoviesList(int page, int category) {
        Log.d(TAG, String.format("Updating movies list with page %d and category %d", page, category));

        movies.addSource(
            LiveDataReactiveStreams.fromPublisher(
                movieRepository.getMovies(String.valueOf(page), category)
                    .toFlowable(BackpressureStrategy.BUFFER)
            ), value -> movies.setValue(value));
    }
}