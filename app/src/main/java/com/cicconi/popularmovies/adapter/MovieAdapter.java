package com.cicconi.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cicconi.popularmovies.Constants;
import com.cicconi.popularmovies.R;
import com.cicconi.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Context context;

    private List<Movie> mMovies = new ArrayList<>();

    final private MovieClickListener mClickListener;

    public MovieAdapter(MovieClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setMoviesData(List<Movie> movies, int page) {
        if(page == Constants.FIRST_PAGE) {
            mMovies = movies;
        } else {
            mMovies.addAll(movies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovies.get(position);

        Picasso.with(context)
            .load(Constants.IMAGE_URL + movie.getPosterPath())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mMovieImageView;

        MovieAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mMovieImageView = itemView.findViewById(R.id.iv_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickListener.onMovieItemClick(mMovies.get(clickedPosition));
        }
    }

    public interface MovieClickListener {
        void onMovieItemClick(Movie movie);
    }
}
