package com.mavericks.myocontroller.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mavericks.myocontroller.R;
import com.mavericks.myocontroller.helpers.Utils;
import com.mavericks.myocontroller.models.Message;
import com.mavericks.myocontroller.models.Video;

/**
 * @author Anurag
 */
public class VideoViewHolder extends RecyclerView.ViewHolder {

    private TextView title;

    public VideoViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
    }

    public void bindData(final Video video, int position) {
        title.setText(video.title);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.link)));
            }
        });
    }
}
