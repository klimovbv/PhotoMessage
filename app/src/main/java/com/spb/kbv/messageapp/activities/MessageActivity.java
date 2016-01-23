package com.spb.kbv.messageapp.activities;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.services.Events;
import com.spb.kbv.messageapp.services.Messages;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.GregorianCalendar;

public class MessageActivity extends BaseAuthenticatedActivity implements View.OnClickListener {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final int REQUEST_IMAGE_DELETED = 100;
    public static final String RESULT_EXTRA_MESSAGE_ID = "RESULT_EXTRA_MESSAGE_ID";
    public static final String EXTRA_MESSAGE_ID = "EXTRA_MESSAGE_ID";

    private static final String STATE_MESSAGE = "STATE_MESSAGE";

    private View drawer;
    private View progressFrame;
    private Message currentMessage;
    private boolean isOpen;
    private int translateOffset;
    private AnimatorSet currentAnimation;
    private TextView translateButton;
    private TextView shortMessage;
    private TextView longMessage;

    @Override
    protected void onMessageAppCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_message);

        drawer = findViewById(R.id.activity_message_drawer);
        translateButton = (TextView) findViewById(R.id.activity_message_translate);
        shortMessage = (TextView) findViewById(R.id.activity_message_shortMessage);
        longMessage = (TextView) findViewById(R.id.activity_message_longMessage);
        progressFrame = findViewById(R.id.activity_message_progressFrame);

        drawer.setOnClickListener(this);

        shortMessage.setText("");
        longMessage.setText("");
        progressFrame.setVisibility(View.GONE);

        toolbar.setNavigationIcon(R.drawable.ic_ab_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMessage(RESULT_OK);
            }
        });

        Message message = null;
        if (savedInstanceState != null){
            message = savedInstanceState.getParcelable(STATE_MESSAGE);
        }

        if (message == null) {
            message = getIntent().getParcelableExtra(EXTRA_MESSAGE);
            if (message == null){
                String id = getIntent().getStringExtra(EXTRA_MESSAGE_ID);
                if (id != null/*.equals("")*/){
                    progressFrame.setVisibility(View.VISIBLE);
                    bus.post(new Messages.GetMessageDetailRequest(id));
                } else {
                    message = new Message(
                            "1",
                            new GregorianCalendar(),
                            "Long Message",
                            null,
                            new UserDetails("1", true, "Person", "person", ""),
                            false,
                            false);
                }
            }
        }

        if (message != null){
            showMessage(message);
        }
    }

    @Subscribe
    public void onMessageDetailsLoaded(final Messages.GetMessageDetailResponse response){
        scheduler.invokeOnResume(response.getClass(), new Runnable() {
            @Override
            public void run() {
                progressFrame.setVisibility(View.GONE);

                if (!response.didSucceed()) {
                    response.showErrorToast(MessageActivity.this);
                    return;
                }
                showMessage(response.message);
            }
        });
    }

    private void showMessage(Message message) {
        currentMessage = message;

        String createdAt = DateUtils.formatDateTime(this, message.getCreatedAt().getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE);
        if (message.isFromUs()){
            getSupportActionBar().setTitle("Sent on " + createdAt);
        } else {
            getSupportActionBar().setTitle("Received on " + createdAt);
        }

        longMessage.setText(message.getLongMessage());

        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
            ImageView image = (ImageView) findViewById(R.id.activity_message_image);
            application.getAuthedPicasso().load(message.getImageUrl()).into(image);
        }

        /*invalidateOptionsMenu();*/
        Intent defaultResult = new Intent();
        defaultResult.putExtra(RESULT_EXTRA_MESSAGE_ID, message.getId());
        setResult(RESULT_OK, defaultResult);

        if (!message.isRead() && !message.isFromUs()){
            bus.post(new Messages.MarkMessageAsReadRequest(message.getId()));
        }
    }

    private void closeMessage (int resultCode){
        Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_MESSAGE_ID, currentMessage.getId());
        setResult(resultCode, data);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MESSAGE, currentMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.acticity_message_menuContact){
            Intent intent = new Intent(this, ContactInfoActivity.class);
            intent.putExtra(ContactInfoActivity.EXTRA_USER_DETAILS, currentMessage.getOtherUser());
            startActivity(intent);
        } else if (item.getItemId() == R.id.acticity_message_menuReply){
            Intent intent = new Intent(this, NewMessageActivity.class);
            intent.putExtra(NewMessageActivity.EXTRA_CONTACT, currentMessage.getOtherUser());
            startActivity(intent);
        } else if (item.getItemId()  == R.id.acticity_message_menuDelete){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete Message")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bus.post(new Messages.DeleteMessageRequest(currentMessage.getId()));
                            closeMessage(REQUEST_IMAGE_DELETED);
                        }
                    })
                    .setCancelable(false)
                    .setNeutralButton("Cancel", null)
                    .create();
            dialog.show();
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.acticity_message_menuReply).setVisible(currentMessage != null && !currentMessage.isFromUs());
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        translateOffset = drawer.getHeight() - translateButton.getHeight();
        drawer.setTranslationY(translateOffset);


    }

    @Override
    public void onClick(View view) {
        isOpen = !isOpen;

        if (currentAnimation != null){
            currentAnimation.cancel();
        }

        int currentBackgroundColor = ((ColorDrawable)drawer.getBackground()).getColor();
        int translationY, color;

        if (isOpen){
            translationY = 0;
            color = Color.parseColor("#EE1998FC");
            translateButton.setText("Close");
        } else {
            translationY = translateOffset;
            color = Color.parseColor("#221998FC");
            translateButton.setText("Translate");
        }

        ObjectAnimator translateAnimator = ObjectAnimator
                .ofFloat(drawer, "translationY", translationY)
                .setDuration(100);

        ObjectAnimator colorAnimator = ObjectAnimator
                .ofInt(drawer, "backgroundColor", currentBackgroundColor, color)
                .setDuration(100);

        colorAnimator.setEvaluator(new ArgbEvaluator());

        currentAnimation = new AnimatorSet();
        currentAnimation.setDuration(300);
        currentAnimation.play(translateAnimator).with(colorAnimator);
        currentAnimation.start();
    }

    @Subscribe
    public void onNotification(Events.OnNotificationReceivedEvent event){
        if (currentMessage == null){
            return;
        }

        if (event.operationType == Events.OPERATION_DELETED &&
                event.entityType == Events.ENTITY_MESSAGE &&
                event.entityId.equals(currentMessage.getId())) {
            closeMessage(REQUEST_IMAGE_DELETED);
        }
    }
}
