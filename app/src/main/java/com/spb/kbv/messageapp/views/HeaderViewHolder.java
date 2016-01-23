package com.spb.kbv.messageapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class HeaderViewHolder extends RecyclerView.ViewHolder{
    private final TextView textView;
    public HeaderViewHolder(LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        textView = (TextView) itemView.findViewById(android.R.id.text1);
    }

    public void populate(String text){
        textView.setText(text);
    }
}
