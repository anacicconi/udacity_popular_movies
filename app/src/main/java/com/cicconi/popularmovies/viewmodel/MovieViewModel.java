package com.cicconi.popularmovies.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.repository.MovieRepository;
import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private static final String TAG = MovieViewModel.class.getSimpleName();

    private MovieRepository movieRepository;
    private MutableLiveData<String> filterPage = new MutableLiveData<>("1");

    public MovieViewModel(@NonNull Application application) {
        super(application);
        movieRepository = new MovieRepository();
    }

    public LiveData<List<Movie>> getPopularMovies() {
        Log.d(TAG, "Retrieving popular movies started");

        return Transformations.switchMap(filterPage,
            newPage -> LiveDataReactiveStreams.fromPublisher(movieRepository.getPopularMovies(newPage).toFlowable()));
    }

    public void setPage(int page) {
        Log.i(TAG, "New page set");
        filterPage.setValue(String.valueOf(page));
    }
}
