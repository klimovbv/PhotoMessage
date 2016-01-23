package com.spb.kbv.messageapp.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.services.entities.Message;
import com.squareup.picasso.Picasso;

public class MessageViewHolder extends RecyclerView.ViewHolder{
    private ImageView avatar;
    private TextView displayName;
    private TextView createdAt;
    private CardView cardView;
    private TextView sentReceived;
    private View backgroundView;


    public MessageViewHolder(View view) {
        super(view);
        cardView = (CardView) view;
        displayName = (TextView) view.findViewById(R.id.list_item_message_displayName);
        createdAt = (TextView) view.findViewById(R.id.list_item_message_createdAt);
        sentReceived = (TextView) view.findViewById(R.id.list_item_message_sentReceived);
        avatar = (ImageView)view.findViewById(R.id.list_item_message_avatar);
        backgroundView = view.findViewById(R.id.list_item_message_background);
    }

    public View getBackgroundView() {
        return backgroundView;
    }

    public void populate(BaseActivity activity, Message message){
        itemView.setTag(message);

        if (message.getOtherUser().getAvatarUrl().isEmpty()){
            avatar.setImageResource(R.drawable.ic_action_person);
        } else {
            activity.getMessageAppApplication().getAuthedPicasso().load(message.getOtherUser().getAvatarUrl()).into(avatar);
        }

        String createdAt = DateUtils.formatDateTime(
                activity.getApplicationContext(),
                message.getCreatedAt().getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        sentReceived.setText(message.isFromUs() ? "sent" : "received");
        displayName.setText(message.getOtherUser().getDisplayName());
        this.createdAt.setText(createdAt);

        int colorResourceId;
        if(message.isSelected()){
            colorResourceId = R.color.list_item_message_background_selected;
            cardView.setCardElevation(5);
        } else if (message.isRead()){
            Log.d("myLogs", " ViewHolder --- Message IS READ " + message.isRead());
            colorResourceId = R.color.list_item_message_background;
            cardView.setCardElevation(2);
        } else {
            Log.d("myLogs", " ViewHolder --- Message IS NOT READ " + message.isRead());
            colorResourceId = R.color.list_item_message_background_unread;
            cardView.setCardElevation(3);
        }

        cardView.setCardBackgroundColor(activity.getApplicationContext().getResources().getColor(colorResourceId));
    }

}
