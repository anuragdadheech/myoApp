package com.mavericks.myocontroller.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mavericks.myocontroller.R;
import com.mavericks.myocontroller.models.Message;

import java.util.ArrayList;

/**
 * @author Anurag
 */
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<Message> conversation = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View conversationView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_conversation, parent, false);
        return new ConversationViewHolder(conversationView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ConversationViewHolder) {
            ConversationViewHolder vh = (ConversationViewHolder) holder;
            Message msg = conversation.get(position);
            vh.bindData(msg, position);
        }
    }

    @Override
    public int getItemCount() {
        return conversation.size();
    }

    public void setConversation(ArrayList<Message> conversation) {
        this.conversation = conversation;
        notifyDataSetChanged();

    }
}
