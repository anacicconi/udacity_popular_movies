package com.cicconi.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cicconi.popularmovies.adapter.ReviewAdapter;
import com.cicconi.popularmovies.adapter.VideoAdapter;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.viewmodel.DetailViewModel;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity implements VideoAdapter.VideoClickListener {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String EXTRA_MOVIE = "movie";
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";

    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mThumbnail;
    private TextView mVideosLabel;
    private TextView mReviewsLabel;

    private DetailViewModel viewModel;

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

        ScrollView mMovieLayout = findViewById(R.id.movie_layout);
        TextView mErrorMessage = findViewById(R.id.tv_error_message);

        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_MOVIE)) {
            Movie movie = (Movie) intent.getExtras().getSerializable(EXTRA_MOVIE);

            if(null == movie) {
                mMovieLayout.setVisibility(View.GONE);
                mErrorMessage.setVisibility(View.VISIBLE);
            } else {
                loadMovie(movie);
                loadVideos(movie.getId());
                loadReviews(movie.getId());
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
            .load(IMAGE_URL + movie.getPosterPath())
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
}
