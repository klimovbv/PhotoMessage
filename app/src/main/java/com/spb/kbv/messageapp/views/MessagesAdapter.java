package com.spb.kbv.messageapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.services.entities.Message;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> implements View.OnClickListener {
    private final LayoutInflater layoutInflater;
    private final BaseActivity activity;
    private final OnMessageClickListener listener;
    private final ArrayList<Message> messages;

    public MessagesAdapter(BaseActivity activity, OnMessageClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        messages = new ArrayList<>();
        layoutInflater = activity.getLayoutInflater();
    }

    public ArrayList<Message> getMessages(){
        return messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item_message, parent, false);
        view.setOnClickListener(this);

        MessageViewHolder viewHolder = new MessageViewHolder(view);
        viewHolder.getBackgroundView().setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.getBackgroundView().setTag(message);
        holder.populate(activity, message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() instanceof Message){
            Message message = (Message) view.getTag();
            listener.onMessageClicked(message);
        }
    }

    public interface OnMessageClickListener {
        void onMessageClicked(Message message);
    }
}
