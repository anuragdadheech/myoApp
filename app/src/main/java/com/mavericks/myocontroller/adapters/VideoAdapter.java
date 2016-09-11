package com.mavericks.myocontroller.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mavericks.myocontroller.R;
import com.mavericks.myocontroller.models.Video;

import java.util.ArrayList;

/**
 * @author Anurag
 */
public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Video> videos = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View VideoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_video, parent, false);
        return new VideoViewHolder(VideoView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoViewHolder) {
            VideoViewHolder vh = (VideoViewHolder) holder;
            Video video = videos.get(position);
            vh.bindData(video, position);
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();

    }
}
