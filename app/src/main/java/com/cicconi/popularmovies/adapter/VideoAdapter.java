package com.cicconi.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cicconi.popularmovies.R;
import com.cicconi.popularmovies.model.Video;
import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private VideoClickListener mClickListener;

    private List<Video> mVideoData  = new ArrayList<>();

    public VideoAdapter(VideoClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setVideoData(List<Video> videoData) {
        mVideoData = videoData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_list_item, parent, false);

        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder holder, int position) {
        String videoName = mVideoData.get(position).getName();
        holder.mTvName.setText(videoName);
        holder.mVideoKey = mVideoData.get(position).getKey();
    }

    @Override
    public int getItemCount() {
        return mVideoData.size();
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTvName;
        String mVideoKey;

        VideoAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTvName = itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onVideoClick(mVideoKey);
        }
    }

    public interface VideoClickListener {
        void onVideoClick(String videoKey);
    }
}
