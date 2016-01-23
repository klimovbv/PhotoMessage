package com.spb.kbv.messageapp.views;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.services.entities.ContactRequest;
import com.squareup.picasso.Picasso;

public class ContactRequestsAdapter extends ArrayAdapter<ContactRequest> {
    private LayoutInflater inflater;
    private Picasso mPicasso;
    public ContactRequestsAdapter(BaseActivity activity) {
        super(activity, 0);
        inflater = activity.getLayoutInflater();
        mPicasso = activity.getMessageAppApplication().getAuthedPicasso();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ContactRequest request = getItem(position);
        ViewHolder holder;


        if(convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_contact_request, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.displayName.setText(request.getUser().getDisplayName());
        if (!request.getUser().getAvatarUrl().isEmpty()) {
            mPicasso.load(request.getUser().getAvatarUrl()).into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.ic_action_person);
        }


        String createdAt = DateUtils.formatDateTime(
                getContext(),
                request.getCreatedAt().getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        if (request.isFromUs()){
            holder.createdAt.setText("Sent at " + createdAt);
        } else {
            holder.createdAt.setText("Received " + createdAt);
        }

        return convertView;
    }


    private class ViewHolder {
        public TextView displayName;
        public TextView createdAt;
        public ImageView avatar;

        public ViewHolder (View view){
            displayName = (TextView)view.findViewById(R.id.list_item_contact_request_displayName);
            createdAt = (TextView)view.findViewById(R.id.list_item_contact_request_createdAt);
            avatar = (ImageView)view.findViewById(R.id.list_item_contact_request_avatar);
        }
    }
}
