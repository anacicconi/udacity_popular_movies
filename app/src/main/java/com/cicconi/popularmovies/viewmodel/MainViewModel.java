package com.cicconi.popularmovies.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.MovieCategory;
import com.cicconi.popularmovies.database.FavoriteMovie;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.repository.MovieRepository;
import io.reactivex.BackpressureStrategy;
import io.reactivex.disposables.CompositeDisposable;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private MovieRepository movieRepository;

    private CompositeDisposable compositeDisposable;

    // Had to use a MediatorLiveData because I have data coming from two different sources
    // and the LiveDataReactiveStreams does not accept a MutableLiveData
    private MediatorLiveData<List<Movie>> movies = new MediatorLiveData<>();

    private LiveData<List<Movie>> allMovies = new MutableLiveData<>();
    private LiveData<List<Movie>> favoriteMovies = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();

        movieRepository = new MovieRepository(context);
        compositeDisposable = new CompositeDisposable();

        Log.d(TAG, "Initializing movies list");

        onAllMoviesSelected(Constants.FIRST_PAGE, MovieCategory.POPULAR);
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void onAllMoviesSelected(int page, MovieCategory category) {
        movies.removeSource(favoriteMovies);
        movies.addSource(
            getAllMovies(page, category), value -> movies.setValue(value)
        );
    }

    private LiveData<List<Movie>> getAllMovies(int page, MovieCategory category) {
        // Getting observable from api and transforming into LiveData
        allMovies = LiveDataReactiveStreams.fromPublisher(
            movieRepository.getMovies(String.valueOf(page), category)
                .toFlowable(BackpressureStrategy.BUFFER)
        );
        return allMovies;
    }

    public void onFavoriteMoviesSelected() {
        movies.removeSource(allMovies);
        movies.addSource(
            getFavoriteMovies(), value -> movies.setValue(value)
        );
    }

    private LiveData<List<Movie>> getFavoriteMovies() {
        // Getting favorite movies from database and transforming into movies
        favoriteMovies = Transformations.map(movieRepository.getFavoriteMovies(), favoriteMovies -> {
                List<Movie> moviesList = new ArrayList<>();

                for (FavoriteMovie favoriteMovie : favoriteMovies) {
                    moviesList.add(favoriteMovie.toMovie());
                }

                return moviesList;
            });

        return favoriteMovies;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
