package com.cicconi.popularmovies;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import com.cicconi.popularmovies.model.Movie;
import com.cicconi.popularmovies.viewmodel.DetailViewModel;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private static final String EXTRA_MOVIE = "movie";
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";

    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mThumbnail;
    private TextView mVideo;
    private TextView mReview;

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
        mVideo = findViewById(R.id.tv_videos);
        mReview = findViewById(R.id.tv_reviews);

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
            }
        }
    }

    private void loadMovie(Movie movie) {
        viewModel.getVideos(movie.getId()).observe(this, videos -> {
            Log.i(TAG, "video live data changed: " + videos.get(0).getName());
            mVideo.setText(videos.get(0).getName());
        });

        viewModel.getReviews(movie.getId()).observe(this, reviews -> {
            Log.i(TAG, "review live data changed: " + reviews.get(0).getAuthor());
            mReview.setText(reviews.get(0).getAuthor());
        });

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
}
