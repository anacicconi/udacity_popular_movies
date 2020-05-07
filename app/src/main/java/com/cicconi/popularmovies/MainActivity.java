package com.cicconi.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cicconi.popularmovies.adapter.MovieAdapter;
import com.cicconi.popularmovies.async.FetchMovieTask;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.viewmodel.MovieViewModel;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_OF_COLUMNS = 2;
    private static final String EXTRA_MOVIE = "movie";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager layoutManager;

    private MovieViewModel viewModel;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;

    private Menu mMainMenu;

    private boolean loading = true;
    final private int firstPage = 1;
    final private int lastPage = 50;
    private int page = firstPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessage = findViewById(R.id.tv_error_message);
        mRecyclerView = findViewById(R.id.recyclerview_movies);

        layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        loadMovies(FetchMovieTask.SORT_POPULAR, firstPage);

        handleRecyclerViewScroll();
    }

    private void handleRecyclerViewScroll() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                            loading = false;
                            if(page != lastPage) {
                                page = page + 1;
                                //loadMovies(FetchMovieTask.SORT_POPULAR, page);
                                viewModel.setPage(page);
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadMovies(int sortType, int page) {
        loadStart();

        /*new FetchMovieTask().getMovies(sortType, String.valueOf(pagination))
            .doOnNext(i -> Log.i(TAG, String.format("Thread loadMovies 1: %s", Thread.currentThread().getName())))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(i -> Log.i(TAG, String.format("Thread loadMovies 2: %s", Thread.currentThread().getName())))
            .doOnNext(this::loadFinish)
            .subscribe();*/

        /*MovieRepository.getPopularMovies(String.valueOf(pagination))
            .subscribeOn(Schedulers.io())
            .doOnNext(i -> Log.i(TAG, String.format("Thread loadMovies 1: %s", Thread.currentThread().getName())))
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(i -> Log.i(TAG, String.format("Thread loadMovies 2: %s", Thread.currentThread().getName())))
            .doOnNext(moviesResponse -> loadFinish(moviesResponse.getResults(), moviesResponse.getPage()))
            .subscribe();*/

        viewModel.setPage(1);
        viewModel.getPopularMovies().observe(this, movies -> {
            Log.i(TAG, "live data changed");
            loadFinish(movies, page);
        });
    }

    private void loadStart() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void loadFinish(List<Movie> movies, int page) {
        if (!movies.isEmpty()) {
            loading = true;
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            showMovieData();

            mMovieAdapter.setMoviesData(movies, page);

            //mRecyclerView.post(() -> mMovieAdapter.setMoviesData(movies, page));

        } else {
            showErrorMessage();
        }
    }

    private void showMovieData() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieItemClick(Movie movie) {
        Intent startChildActivityIntent = new Intent(this, DetailsActivity.class);
        startChildActivityIntent.putExtra(EXTRA_MOVIE, movie);
        startActivity(startChildActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMainMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, mMainMenu);

        enableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));
        disableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            //loadMovies(FetchMovieTask.SORT_POPULAR, firstPage);
            mRecyclerView.scrollToPosition(0);

            enableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));

            return true;
        }

        if (id == R.id.action_sort_rating) {
            //loadMovies(FetchMovieTask.SORT_RATING, firstPage);
            mRecyclerView.scrollToPosition(0);

            enableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableMenuItem(MenuItem item) {
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
        item.setTitle(spanString);
    }

    private void disableMenuItem(MenuItem item) {
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spanString.length(), 0);
        item.setTitle(spanString);
    }
}
