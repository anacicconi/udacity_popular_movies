package com.cicconi.popularmovies.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.cicconi.popularmovies.model.Movie;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Context context;

    private final Movie movie;

    public DetailsViewModelFactory(Context context, Movie movie) {
        this.context = context;
        this.movie = movie;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailsViewModel(context, movie);
    }
}
