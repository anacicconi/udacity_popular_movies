package com.cicconi.popularmovies.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

class MovieLiveData extends MediatorLiveData<MovieFilter> {

    MovieLiveData(LiveData<Integer> page, LiveData<Integer> category) {
        addSource(page, newPage -> {
            System.out.println("page: " + page.getValue());
            System.out.println("page: " + newPage);
            setValue(new MovieFilter(newPage, category.getValue()));
        });

        addSource(category, newCategory -> {
            System.out.println("category: " + category.getValue());
            System.out.println("category: " + newCategory);
            setValue(new MovieFilter(page.getValue(), newCategory));
        });
    }
}
