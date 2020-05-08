package com.cicconi.popularmovies.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import com.cicconi.popularmovies.model.Review;
import com.cicconi.popularmovies.model.Video;
import com.cicconi.popularmovies.repository.MovieRepository;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    private static final String TAG = DetailViewModel.class.getSimpleName();

    private MovieRepository movieRepository;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        movieRepository = new MovieRepository();
    }

    public LiveData<List<Video>> getVideos(int id) {
        Log.i(TAG, String.format("Getting videos of movie %d", id));

        return LiveDataReactiveStreams.fromPublisher(
            movieRepository.getVideosById(id)
                .toFlowable(BackpressureStrategy.BUFFER)
        );
    }

    public LiveData<List<Review>> getReviews(int id) {
        Log.i(TAG, String.format("Getting reviews of movie %d", id));

        return LiveDataReactiveStreams.fromPublisher(
            movieRepository.getReviewsById(id)
                .toFlowable(BackpressureStrategy.BUFFER)
        );
    }
}
