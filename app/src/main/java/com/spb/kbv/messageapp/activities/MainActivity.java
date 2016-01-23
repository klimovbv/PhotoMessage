package com.spb.kbv.messageapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.Contacts;
import com.spb.kbv.messageapp.services.Events;
import com.spb.kbv.messageapp.services.Messages;
import com.spb.kbv.messageapp.services.entities.ContactRequest;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.spb.kbv.messageapp.views.MainActivityAdapter;
import com.spb.kbv.messageapp.views.MainNavDrawer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends BaseAuthenticatedActivity implements View.OnClickListener, MainActivityAdapter.MainActivityListener {
    private static final int REQUEST_SHOW_MESSAGE = 1;
    private MainActivityAdapter adapter;
    private List<Message> messages;
    private List<ContactRequest> contactRequests;

    @Override
    protected void onMessageAppCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Inbox");

        setNavDrawer(new MainNavDrawer(this));

        findViewById(R.id.activity_main_newMessageButton).setOnClickListener(this);

        adapter = new MainActivityAdapter(this, this);
        messages = adapter.getMessages();
        contactRequests = adapter.getContactRequests();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        recyclerView.setAdapter(adapter);

        if (isTablet) {
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(manager);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return 2;
                    }

                    if (contactRequests.size() > 0 && position == contactRequests.size() + 1) {
                        return 2;
                    }

                    return 1;
                }
            });
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        scheduler.invokeEveryMilliseconds(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        }, 1000 * 60 * 3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.post(new Messages.SearchMessagesRequest(false, true));
        bus.post(new Contacts.GetContactRequestRequest(false));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.activity_main_newMessageButton){
            startActivity(new Intent(this, NewMessageActivity.class));
        }
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        bus.post(new Messages.SearchMessagesRequest(false, true));
        bus.post(new Contacts.GetContactRequestRequest(false));
    }

    @Subscribe
    public void onMessagesLoaded(final Messages.SearchMessageResponse response){
        scheduler.invokeOnResume(response.getClass(), new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);

                if (!response.didSucceed()) {
                    response.showErrorToast(MainActivity.this);
                    return;
                }
                messages.clear();
                messages.addAll(response.messages);

                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o2, Message o1) {
                        return Long.toString(o1.getCreatedAt().getTimeInMillis())
                                .compareTo(Long.toString(o2.getCreatedAt().getTimeInMillis()));
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onContactRequestsLoaded (final Contacts.GetContactRequestResponse response){
        scheduler.invokeOnResume(response.getClass(), new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);

                if (!response.didSucceed()){
                    response.showErrorToast(MainActivity.this);
                    return;
                }

                contactRequests.clear();
                contactRequests.addAll(response.requests);
            }
        });
    }

    @Override
    public void onMessageClicked(Message message) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_SHOW_MESSAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHOW_MESSAGE && data != null){
            String messageId = data.getStringExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID);
            if (messageId.isEmpty()) {
                return;
            }
            for (int i = 0; i < messages.size(); i++){
                Message message = messages.get(i);
                if (message.getId() == messageId){
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

    @Override
    public void onContactRequestClicked(final ContactRequest request, final int position) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_display, null);
        ImageView avatar = (ImageView) dialogView.findViewById(R.id.dialog_user_display_avatar);
        TextView displayName = (TextView) dialogView.findViewById(R.id.dialog_user_display_displayName);

        UserDetails user = request.getUser();
        displayName.setText(user.getDisplayName());
        if (user.getAvatarUrl().isEmpty()){
            avatar.setImageResource(R.drawable.ic_action_person);
        } else {
            getMessageAppApplication().getAuthedPicasso().load(user.getAvatarUrl()).into(avatar);
        }

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == dialog.BUTTON_NEUTRAL){
                    return;
                }

                boolean doAccept = which == dialog.BUTTON_POSITIVE;
                contactRequests.remove(request);
                adapter.notifyItemRemoved(position + 1);

                if (contactRequests.size() == 0){
                    adapter.notifyItemRemoved(0);
                }

                bus.post(new Contacts.RespondToContactRequestRequest(request.getUser().getUsername()/*getId()*/, doAccept));
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Respond To Contact Request")
                .setView(dialogView)
                .setPositiveButton("Accept", clickListener)
                .setNeutralButton("Cancel", clickListener)
                .setNegativeButton("Reject", clickListener)
                .setCancelable(false)
                .create();

        dialog.show();
    }

    @Subscribe
    public void onNotification(final Events.OnNotificationReceivedEvent event){
        scheduler.invokeOnResume(event.getClass(), new Runnable() {
            @Override
            public void run() {
                if (event.entityOwnerId == application.getAuth().getUser().getId()){
                    return;
                }

                if (event.entityType == Events.ENTITY_MESSAGE) {
                    if (event.operationType == Events.OPERATION_CREATED){
                        bus.post(new Messages.SearchMessagesRequest(false, true));
                    } else {
                        for (int i = 0; i < messages.size(); i++){
                            if (messages.get(i).getId() == event.entityId){
                                messages.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else if (event.entityType == Events.ENTITY_CONTACT_REQUEST){
                    if (event.operationType == Events.OPERATION_CREATED) {
                        bus.post(new Contacts.GetContactRequestRequest(false));
                    } else {
                        for (int i = 0; i < contactRequests.size(); i++){
                            if (contactRequests.get(i).getUser().getId().equals(event.entityId)){
                                contactRequests.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
}
