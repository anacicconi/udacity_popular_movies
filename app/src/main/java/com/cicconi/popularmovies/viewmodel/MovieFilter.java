package com.cicconi.popularmovies.viewmodel;

public class MovieFilter {
    private int page;
    private int category;

    MovieFilter(Integer page, Integer category) {
        this.page = page;
        this.category = category;
    }

    public int getPage() {
        return page;
    }

    public int getCategory() {
        return category;
    }
}
