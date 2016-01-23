package com.spb.kbv.messageapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.Messages;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.views.MainNavDrawer;
import com.spb.kbv.messageapp.views.MessagesAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class SentMessagesActivity extends BaseAuthenticatedActivity implements MessagesAdapter.OnMessageClickListener {
    private static final int REQUEST_VIEW_MESSAGE = 1;
    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private View progressFrame;

    @Override
    protected void onMessageAppCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sent_messages);
        setNavDrawer(new MainNavDrawer(this));
        getSupportActionBar().setTitle("Sent Messages");

        adapter = new MessagesAdapter(this, this);
        messages = adapter.getMessages();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.activity_sent_messages_messages);
        recyclerView.setAdapter(adapter);

        if (isTablet){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        progressFrame = findViewById(R.id.activity_sent_messages_progressFrame);

        scheduler.postEveryMilliseconds(new Messages.SearchMessagesRequest(true, false), 1000 * 60 * 3);
    }

    @Override
    public void onMessageClicked(Message message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_VIEW_MESSAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_VIEW_MESSAGE || resultCode != MessageActivity.REQUEST_IMAGE_DELETED){
            return;
        }

        String messageId = data.getStringExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID);
        if (messageId.isEmpty()){
            return;
        }
        for (int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            if (!message.getId().equals(messageId)){
                continue;
            }

            messages.remove(i);
            adapter.notifyItemRemoved(i);
            break;
        }
    }

    @Subscribe
    public void onMessagesLoaded(Messages.SearchMessageResponse response){
        progressFrame.setVisibility(View.GONE);

        if(!response.didSucceed()){
            response.showErrorToast(this);
            return;
        }


        int oldMessagesSize = messages.size();
        messages.clear();
        adapter.notifyItemRangeRemoved(0, oldMessagesSize);

        messages.addAll(response.messages);

        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o2, Message o1) {
                return Long.toString(o1.getCreatedAt().getTimeInMillis()).compareTo(Long.toString(o2.getCreatedAt().getTimeInMillis()));
            }
        });

        adapter.notifyItemRangeInserted(0, messages.size());

    }
}