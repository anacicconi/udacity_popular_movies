package com.cicconi.popularmovies.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.MovieCategory;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.repository.MovieRepository;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private MovieRepository movieRepository;

    private LiveData<List<Movie>> allMovies;
    private LiveData<List<Movie>> favoriteMovies;

    // Had to use a MediatorLiveData because I have data coming from two different sources (database and api)
    private MediatorLiveData<List<Movie>> movies = new MediatorLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();

        movieRepository = new MovieRepository(context);

        Log.i(TAG, "Initializing movies lists");

        allMovies = movieRepository.getMovies(String.valueOf(Constants.FIRST_PAGE), MovieCategory.POPULAR);
        favoriteMovies = movieRepository.getFavoriteMovies();

        // Adding only popular movies to the mediator
        movies.addSource(allMovies, value -> movies.setValue(value));
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void onAllMoviesSelected(int page, MovieCategory category) {
        resetMoviesSource();
        // need to set the allMovies list again because the source can change accordingly to the parameters
        allMovies = movieRepository.getMovies(String.valueOf(page), category);
        movies.addSource(allMovies, value2 -> movies.setValue(value2));
    }

    public void onFavoriteMoviesSelected() {
        resetMoviesSource();
        // favoriteMovies has always the same source so no need to set it again, just add it as a source
        movies.addSource(favoriteMovies, value -> movies.setValue(value));
    }

    // Reset the values on the MediatorLiveData.
    // Otherwise, it accumulates all sources.
    private void resetMoviesSource() {
        movies.removeSource(allMovies);
        movies.removeSource(favoriteMovies);
    }
}
