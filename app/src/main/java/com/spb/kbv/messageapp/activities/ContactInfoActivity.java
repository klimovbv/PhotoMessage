package com.spb.kbv.messageapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.Contacts;
import com.spb.kbv.messageapp.services.Messages;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.spb.kbv.messageapp.views.MessagesAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContactInfoActivity extends BaseAuthenticatedActivity implements MessagesAdapter.OnMessageClickListener {
    public static final String EXTRA_USER_DETAILS = "EXTRA_USER_DETAILS";
    public static final int RESULT_USER_REMOVED = 101;

    private static final int REQUEST_SEND_MESSAGE = 1;
    private static final int REQUEST_SHOW_MESSAGE = 2;

    private UserDetails userDetails;
    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private View progressFrame;
    private RecyclerView recyclerView;


    @Override
    protected void onMessageAppCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contacts_info);

        userDetails = getIntent().getParcelableExtra(EXTRA_USER_DETAILS);
        if (userDetails.getDisplayName() == null) {
            userDetails = new UserDetails("1", true, "A contact", "a_contact", "http://www.gravatar.com/avatar/1.jpg");
        }

        getSupportActionBar().setTitle(userDetails.getDisplayName());
        toolbar.setNavigationIcon(R.drawable.ic_ab_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new MessagesAdapter(this, this);
        messages = adapter.getMessages();

        progressFrame = findViewById(R.id.activity_contacts_info_progressFrame);

        recyclerView = (RecyclerView) findViewById(R.id.activity_contacts_info_messages);
        if (isTablet) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        recyclerView.setAdapter(adapter);

        scheduler.postEveryMilliseconds(new Messages.SearchMessagesRequest(userDetails.getUsername(), true, true), 1000 * 60 * 3);
    }

    @Override
    public void onMessageClicked(Message message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_SHOW_MESSAGE);
    }

    @Subscribe
    public void onMessagesReceived(final Messages.SearchMessageResponse respones){
        scheduler.invokeOnResume(Messages.SearchMessageResponse.class, new Runnable() {
            @Override
            public void run() {
                progressFrame.setVisibility(View.GONE);
                if (!respones.didSucceed()) {
                    respones.showErrorToast(ContactInfoActivity.this);
                    return;
                }

                int oldSize = messages.size();
                messages.clear();
                adapter.notifyItemRangeRemoved(0, oldSize);

                messages.addAll(respones.messages);

                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o2, Message o1) {
                        return Long.toString(o1.getCreatedAt().getTimeInMillis())
                                .compareTo(Long.toString(o2.getCreatedAt().getTimeInMillis()));
                    }
                });

                adapter.notifyItemRangeInserted(0, messages.size());
            }
        });

    }
    private void doRemoveContact(){
        progressFrame.setVisibility(View.VISIBLE);
        bus.post(new Contacts.RemoveContactRequest(userDetails.getUsername()/*getId()*/));
    }

    @Subscribe
    public void onRemoveContact(final Contacts.RemoveContactResponse response){
        scheduler.invokeOnResume(Contacts.RemoveContactResponse.class, new Runnable() {
            @Override
            public void run() {
                if (!response.didSucceed()) {
                    response.showErrorToast(ContactInfoActivity.this);
                    progressFrame.setVisibility(View.VISIBLE);
                    return;
                }
                setResult(RESULT_USER_REMOVED);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contacts_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.activity_contact_info_menuNewMessage) {
            Intent intent = new Intent(this, NewMessageActivity.class);
            intent.putExtra(NewMessageActivity.EXTRA_CONTACT, userDetails);
            startActivityForResult(intent, REQUEST_SEND_MESSAGE);
            return true;
        }

        if (itemId == R.id.activity_contact_info_menuRemoveFriend) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Remove Friend")
                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doRemoveContact();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SEND_MESSAGE && resultCode == RESULT_OK) {
            progressFrame.setVisibility(View.VISIBLE);
            bus.post(new Messages.SearchMessagesRequest(userDetails.getId(), true, true));
        }
        if (requestCode == REQUEST_SHOW_MESSAGE && data != null){
            String messageId = data.getStringExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID/*, "-1"*/);
            if (messageId.isEmpty()) {
                return;
            }
            for (int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);
                if (message.getId().equals(messageId)){
                    if (resultCode == MessageActivity.REQUEST_IMAGE_DELETED){
                        messages.remove(message);
                    } else {
                        message.setIsRead(true);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}
