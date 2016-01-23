package com.spb.kbv.messageapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.services.entities.ContactRequest;
import com.spb.kbv.messageapp.services.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_CONTACT_REQUEST = 2;
    private static final int VIEW_TYPE_HEADER = 3;

    private List<Message> messages;
    private List<ContactRequest> contactRequests;
    private MainActivityListener listener;
    private LayoutInflater inflater;
    private BaseActivity activity;

    public MainActivityAdapter(MainActivityListener listener, BaseActivity activity) {
        this.listener = listener;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        messages = new ArrayList<>();
        contactRequests = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<ContactRequest> getContactRequests() {
        return contactRequests;
    }

    @Override
    public int getItemViewType(int position) {
        if (contactRequests.size() > 0){
            if (position == 0){
                return VIEW_TYPE_HEADER;
            }

            position--;

            if (position < contactRequests.size()){
                return VIEW_TYPE_CONTACT_REQUEST;
            }

            position -= contactRequests.size();
        }

        if (messages.size() > 0){
            if (position == 0){
                return VIEW_TYPE_HEADER;
            }

            position--;

            if (position < messages.size()){
                return VIEW_TYPE_MESSAGE;
            }

            position -= contactRequests.size();
        }

        throw new IllegalArgumentException("We are being asked for an item type from position " + position +
        ", though we have no such item.");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE){
            final MessageViewHolder viewHolder = new MessageViewHolder(inflater.inflate(R.layout.list_item_message, parent, false));
            viewHolder.getBackgroundView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageClicked((Message)view.getTag());
                }
            });
            return viewHolder;
        } else if (viewType == VIEW_TYPE_CONTACT_REQUEST){
            final ContactRequestViewHolder viewHolder = new ContactRequestViewHolder(inflater, parent);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContactRequest request = (ContactRequest) viewHolder.itemView.getTag();
                    listener.onContactRequestClicked(request, contactRequests.indexOf(request));
                }
            });
            return viewHolder;
        } else if (viewType == VIEW_TYPE_HEADER){
            return new HeaderViewHolder(inflater, parent);
        }

        throw new IllegalArgumentException("ViewType " + viewType + " id not supported");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContactRequestViewHolder){
            position--;

            ContactRequest request = contactRequests.get(position);
            holder.itemView.setTag(request);
            ((ContactRequestViewHolder) holder).populate(activity, request);
        } else if (holder instanceof MessageViewHolder) {
            position--;

            if (contactRequests.size() > 0){
                position = position - 1 - contactRequests.size();
            }

            Message message = messages.get(position);
            MessageViewHolder viewHolder = (MessageViewHolder) holder;
            viewHolder.getBackgroundView().setTag(message);
            viewHolder.populate(activity, message);
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;

            if (position == 0 && contactRequests.size() > 0) {
                viewHolder.populate("Received Contact Requests");
            } else {
                viewHolder.populate("Received Messages");
            }
        } else {
             throw new IllegalArgumentException("Cannot populate holder of type " + holder.getClass().getName());
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (contactRequests.size() > 0) {
            count += 1 + contactRequests.size();
        }

        if (messages.size() > 0) {
            count += 1 + getMessages().size();
        }

        return count;
    }

    public interface MainActivityListener {
        void onMessageClicked(Message message);
        void onContactRequestClicked(ContactRequest request, int position);
    }
}
