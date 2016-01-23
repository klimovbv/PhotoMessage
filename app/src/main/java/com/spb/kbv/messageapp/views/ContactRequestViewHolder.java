package com.spb.kbv.messageapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.services.entities.ContactRequest;
import com.squareup.picasso.Picasso;

public class ContactRequestViewHolder extends RecyclerView.ViewHolder{
    private TextView displayName;
    private TextView createdAt;
    private ImageView avatar;
    public ContactRequestViewHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_item_contact_request, parent, false));

        displayName = (TextView) itemView.findViewById(R.id.list_item_contact_request_displayName);
        createdAt = (TextView) itemView.findViewById(R.id.list_item_contact_request_createdAt);
        avatar = (ImageView) itemView.findViewById(R.id.list_item_contact_request_avatar);
    }

    public void populate (BaseActivity activity, ContactRequest request) {
        displayName.setText(request.getUser().getDisplayName());
        if (request.getUser().getAvatarUrl().isEmpty()){
            avatar.setImageResource(R.drawable.ic_action_person);
        } else {
            activity.getMessageAppApplication().getAuthedPicasso().load(request.getUser().getAvatarUrl()).into(avatar);
        }

        String dateText = DateUtils.formatDateTime(
                activity.getApplicationContext(),
                request.getCreatedAt().getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        if (request.isFromUs()){
            createdAt.setText("Sent at " + dateText);
        } else {
            createdAt.setText("Received at " + dateText);
        }
    }
}
