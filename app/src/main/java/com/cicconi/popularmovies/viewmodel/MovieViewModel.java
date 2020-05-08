package com.cicconi.popularmovies.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.repository.MovieRepository;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private static final String TAG = MovieViewModel.class.getSimpleName();

    private MovieRepository movieRepository;
    private MutableLiveData<Integer> page = new MutableLiveData<>(1);
    private MutableLiveData<Integer> category = new MutableLiveData<>(Constants.SORT_POPULAR);

    public MovieViewModel(@NonNull Application application) {
        super(application);
        movieRepository = new MovieRepository();
    }

    public LiveData<List<Movie>> getMovies() {
        MovieLiveData trigger = new MovieLiveData(page, category);

        return Transformations.switchMap(trigger,
            movieFilter -> LiveDataReactiveStreams.fromPublisher(
                movieRepository.getMovies(String.valueOf(movieFilter.getPage()), movieFilter.getCategory())
                    .toFlowable(BackpressureStrategy.BUFFER)
            ));
    }

    /*public LiveData<List<Movie>> getPopularMovies() {
        Log.d(TAG, "Retrieving popular movies started");

        return Transformations.switchMap(page,
            newPage -> LiveDataReactiveStreams.fromPublisher(
                movieRepository.getPopularMovies(newPage)
                    .toFlowable(BackpressureStrategy.BUFFER)
            )
        );
    }*/

    public void setPage(int value) {
        Log.i(TAG, String.format("New page set: %d", value));
        page.setValue(value);
    }

    public void setCategory(int value) {
        Log.i(TAG, String.format("New category set %d", value));
        page.setValue(1);
        category.setValue(value);
    }
}
