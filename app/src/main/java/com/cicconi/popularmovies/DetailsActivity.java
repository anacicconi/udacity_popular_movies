package com.cicconi.popularmovies;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.cicconi.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE = "movie";
    private static final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";

    private TextView mTitle;
    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mTitle = findViewById(R.id.tv_title);
        mSynopsis = findViewById(R.id.tv_synopsis);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mRating = findViewById(R.id.tv_rating);
        mThumbnail = findViewById(R.id.iv_thumbnail);

        ScrollView mMovieLayout = findViewById(R.id.movie_layout);
        TextView mErrorMessage = findViewById(R.id.tv_error_message);

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
        mTitle.setText(movie.getTitle());
        mSynopsis.setText(movie.getSynopsis());
        mReleaseDate.setText(movie.getReleaseDate());
        mRating.setText(movie.getRating());

        Picasso.with(this)
            .load(IMAGE_URL + movie.getImage())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(mThumbnail);
    }
}
