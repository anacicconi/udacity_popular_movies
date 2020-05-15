package com.cicconi.popularmovies;

import android.content.Intent;
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
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.viewmodel.MainViewModel;
import com.facebook.stetho.Stetho;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_OF_COLUMNS = 2;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private GridLayoutManager layoutManager;

    private MainViewModel viewModel;

    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;
    private TextView mNoResultsMessage;

    private Menu mMainMenu;

    private boolean loading = true;
    final private int lastPage = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Used to debug database content
        //Stetho.initializeWithDefaults(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessage = findViewById(R.id.tv_error_message);
        mNoResultsMessage = findViewById(R.id.tv_no_results_message);
        mRecyclerView = findViewById(R.id.recyclerview_movies);

        layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        loadMovies();
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
                // No need to handle pagination for favorite movies
                if (loading && viewModel.getCategory() != MovieCategory.FAVORITE) {
                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                        loading = false;
                        if(viewModel.getPage() != lastPage) {
                            viewModel.incrementPage();
                            viewModel.onAllMoviesSelected();
                        }
                    }
                }
            }
            }
        });
    }

    private void loadMovies() {
        loadStart();

        viewModel.getMovies().observe(this, movies -> {
            Log.i(TAG, "movie live data changed");
            loadFinish(movies);
        });
    }

    private void loadStart() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void loadFinish(List<Movie> movies) {
        if (!movies.isEmpty()) {
            loading = true;

            if(viewModel.getPage() == Constants.FIRST_PAGE) {
                mRecyclerView.scrollToPosition(0);
            }

            showMovieView();
            mMovieAdapter.setMoviesData(movies, viewModel.getPage());
        } else {
            showErrorMessage();
        }
    }

    private void showMovieView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mNoResultsMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        if(viewModel.getCategory() == MovieCategory.FAVORITE) {
            mNoResultsMessage.setVisibility(View.VISIBLE);
        } else {
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void updateCategory(MovieCategory newCategory) {
        viewModel.resetPage();
        viewModel.setCategory(newCategory);
    }

    @Override
    public void onMovieItemClick(Movie movie) {
        // Have to reset the category to popular here because once the user comes back
        // from a detail activity he arrives in the popular list
        Intent startChildActivityIntent = new Intent(this, DetailsActivity.class);
        startChildActivityIntent.putExtra(Constants.EXTRA_MOVIE, movie);
        startActivity(startChildActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMainMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, mMainMenu);

        enableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));
        disableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));
        disableMenuItem(mMainMenu.findItem(R.id.action_sort_favorite));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popular) {
            updateCategory(MovieCategory.POPULAR);
            viewModel.onAllMoviesSelected();

            enableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_favorite));

            return true;
        }

        if (id == R.id.action_sort_rating) {
            updateCategory(MovieCategory.TOP_RATED);
            viewModel.onAllMoviesSelected();

            enableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_favorite));

            return true;
        }

        if (id == R.id.action_sort_favorite) {
            updateCategory(MovieCategory.FAVORITE);
            viewModel.onFavoriteMoviesSelected();

            enableMenuItem(mMainMenu.findItem(R.id.action_sort_favorite));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_popular));
            disableMenuItem(mMainMenu.findItem(R.id.action_sort_rating));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableMenuItem(MenuItem item) {
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, spanString.length(), 0);
        item.setTitle(spanString);
    }

    private void disableMenuItem(MenuItem item) {
        SpannableString spanString = new SpannableString(item.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSecondaryText)), 0, spanString.length(), 0);
        item.setTitle(spanString);
    }
}
