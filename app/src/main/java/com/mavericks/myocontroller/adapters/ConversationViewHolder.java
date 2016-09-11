package com.mavericks.myocontroller.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mavericks.myocontroller.R;
import com.mavericks.myocontroller.helpers.Utils;
import com.mavericks.myocontroller.models.Message;

/**
 * @author Anurag
 */
public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private TextView messageText;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
    }

    public void bindData(Message message, int position) {
        messageText.setText(message.data);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) messageText.getLayoutParams();
        if (message.isSpeaker) {
            params.gravity = Gravity.RIGHT;
            params.setMarginStart(Utils.convertDPTOPixels(itemView.getContext(), 20));
            messageText.setLayoutParams(params);
            messageText.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.speaker));
        } else {
            params.gravity = Gravity.LEFT;
            params.setMarginEnd(Utils.convertDPTOPixels(itemView.getContext(), 20));
            messageText.setLayoutParams(params);
            messageText.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.listener));
        }
    }
}
