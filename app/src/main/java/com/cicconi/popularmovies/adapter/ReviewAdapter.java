package com.cicconi.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cicconi.popularmovies.R;
import com.cicconi.popularmovies.model.Review;
import com.cicconi.popularmovies.model.Video;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Review> mReviewData  = new ArrayList<>();

    public void setReviewData(List<Review> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_list_item, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        String author = mReviewData.get(position).getAuthor();
        String content = mReviewData.get(position).getContent();
        holder.mTvAuthor.setText(author);
        holder.mTvContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return mReviewData.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView mTvAuthor;
        final TextView mTvContent;

        ReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvAuthor = itemView.findViewById(R.id.tv_author);
            this.mTvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
