package com.spb.kbv.messageapp.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.squareup.picasso.Picasso;

public class UserDetailsAdapter extends ArrayAdapter<UserDetails>{
    private LayoutInflater inflater;
    public Picasso mPicasso;


    public UserDetailsAdapter (BaseActivity activity) {
        super(activity, 0);
        inflater = activity.getLayoutInflater();
        mPicasso = activity.getMessageAppApplication().getAuthedPicasso();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        UserDetails details = getItem(position);

        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_item_user_details, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.displayName.setText(details.getDisplayName());
        if (!details.getAvatarUrl().isEmpty()){
            mPicasso.load(details.getAvatarUrl()).into(holder.avatar);
        } else {
        holder.avatar.setImageResource(R.drawable.ic_action_person);
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView displayName;
        public ImageView avatar;

        public ViewHolder(View view){
            this.displayName = (TextView) view.findViewById(R.id.list_item_user_details_displayName);
            this.avatar = (ImageView) view.findViewById(R.id.list_item_user_details_avatar);
        }
    }
}
