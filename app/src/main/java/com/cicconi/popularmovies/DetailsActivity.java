package com.cicconi.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cicconi.popularmovies.adapter.ReviewAdapter;
import com.cicconi.popularmovies.adapter.VideoAdapter;
import com.cicconi.popularmovies.database.AppDatabase;
import com.cicconi.popularmovies.database.FavoriteMovie;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.viewmodel.DetailViewModel;
import com.squareup.picasso.Picasso;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailsActivity extends AppCompatActivity implements VideoAdapter.VideoClickListener {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mThumbnail;
    private TextView mVideosLabel;
    private TextView mReviewsLabel;
    private ImageView mFavoriteIcon;

    private DetailViewModel viewModel;

    private AppDatabase mDb;

    private Movie movie;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitle = findViewById(R.id.tv_title);
        mSynopsis = findViewById(R.id.tv_synopsis);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mRating = findViewById(R.id.tv_rating);
        mThumbnail = findViewById(R.id.iv_thumbnail);
        mVideosLabel = findViewById(R.id.tv_videos);
        mReviewsLabel = findViewById(R.id.tv_reviews);
        mFavoriteIcon = findViewById(R.id.iv_favorite);

        ScrollView mMovieLayout = findViewById(R.id.movie_layout);
        TextView mErrorMessage = findViewById(R.id.tv_error_message);

        compositeDisposable = new CompositeDisposable();

        mDb = AppDatabase.getInstance(getApplicationContext());

        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_MOVIE)) {
            movie = (Movie) intent.getExtras().getSerializable(Constants.EXTRA_MOVIE);

            if(null == movie) {
                mMovieLayout.setVisibility(View.GONE);
                mErrorMessage.setVisibility(View.VISIBLE);
            } else {
                loadMovie(movie);
                loadVideos(movie.getId());
                loadReviews(movie.getId());
                onFavoriteIconClick();
            }
        }

    }

    private void loadMovie(Movie movie) {
        String title = movie.getTitle();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();
        Double voteAverage = movie.getVoteAverage();

        if(null != title) {
            mTitle.setText(title);
        } else {
            mTitle.setText(R.string.unknown);
        }

        if(null != overview) {
            mSynopsis.setText(overview);
        } else {
            mSynopsis.setText(R.string.unknown);
        }

        if(null != releaseDate) {
            mReleaseDate.setText(releaseDate);
        } else {
            mReleaseDate.setText(R.string.unknown);
        }

        if(null != voteAverage) {
            mRating.setText(String.valueOf(voteAverage));
        } else {
            mRating.setText(R.string.no_rating);
        }

        Picasso.with(this)
            .load(Constants.IMAGE_URL + movie.getPosterPath())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(mThumbnail);
    }

    private void loadVideos(Integer videoId) {
        RecyclerView mVideoRecyclerView = findViewById(R.id.recyclerview_videos);
        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mVideoRecyclerView.setLayoutManager(videoLayoutManager);
        mVideoRecyclerView.setHasFixedSize(true);

        VideoAdapter mVideoAdapter = new VideoAdapter(this);
        mVideoRecyclerView.setAdapter(mVideoAdapter);

        viewModel.getVideos(videoId).observe(this, videos -> {
            Log.i(TAG, "video live data changed");
            if (!videos.isEmpty()) {
                mVideosLabel.setVisibility(View.VISIBLE);
                mVideoAdapter.setVideoData(videos);
            }
        });
    }

    private void loadReviews(Integer videoId) {
        RecyclerView mReviewRecyclerView = findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setHasFixedSize(true);

        ReviewAdapter mReviewAdapter = new ReviewAdapter();
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        viewModel.getReviews(videoId).observe(this, reviews -> {
            Log.i(TAG, "video live data changed");
            if (!reviews.isEmpty()) {
                mReviewsLabel.setVisibility(View.VISIBLE);
                mReviewAdapter.setReviewData(reviews);
            }
        });
    }

    @Override
    public void onVideoClick(String videoKey) {
        String url = Constants.YOUTUBE_URL + videoKey;
        Uri webPage = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void onFavoriteIconClick() {
        mFavoriteIcon.setOnClickListener(view -> {
            mFavoriteIcon.setColorFilter(getResources().getColor(R.color.colorFavorite));

            FavoriteMovie favoriteMovie = new FavoriteMovie(
                movie.getId(),
                movie.getOriginalTitle(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getPopularity(),
                movie.getPosterPath()
            );

            Completable query = mDb.favoriteMovieDAO().insertFavoriteMovie(favoriteMovie);

            Disposable disposable = query
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    e.printStackTrace();
                    Toast.makeText(DetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                })
                .subscribe(
                    () -> Log.d(TAG, "New favorite movie saved"),
                    Throwable::printStackTrace
                );

            compositeDisposable.add(disposable);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
