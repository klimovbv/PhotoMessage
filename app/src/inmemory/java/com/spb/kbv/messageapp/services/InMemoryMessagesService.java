package com.spb.kbv.messageapp.services;

import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class InMemoryMessagesService extends BaseInMemoryService {
    public InMemoryMessagesService(MessageApplication application) {
        super(application);
    }

    @Subscribe
    public void onDeleteMessage (Messages.DeleteMessageRequest request){


        Messages.DeleteMessageResponse response = new Messages.DeleteMessageResponse();
        response.messageId = request.messageId;
        postDelayed(response);
    }

    @Subscribe
    public void searchMessages(Messages.SearchMessagesRequest request){
        Messages.SearchMessageResponse response = new Messages.SearchMessageResponse();
        response.messages = new ArrayList<>();

        UserDetails[] users = new UserDetails[10];
        for (int i = 0; i < users.length; i++){
            String stringId = Integer.toString(i);
            users[i] = new UserDetails(
                    i,
                    true,
                    "User " + stringId,
                    "user_" + stringId,
                    "http://www.gravatar.com/avatar/" + stringId + "?d=identicon");
        }
        Random random = new Random();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -100);

        for (int i =0; i < 100; i++){
            boolean isFromUs;

            if (request.includeReceivedMessages && request.includeSentMessages){
                isFromUs = random.nextBoolean();
            } else {
                isFromUs = !request.includeReceivedMessages;
            }
            date.set(Calendar.MINUTE, random.nextInt(60 * 24));

            String numberString = Integer.toString(i);
            response.messages.add(new Message(
                    i,
                    (Calendar) date.clone(),
                    "Short Message " + numberString,
                    "Long Message " + numberString,
                    "",
                    users[random.nextInt(users.length)],
                    isFromUs,
                    i > 4));
        }
        postDelayed(response, 2000);
    }

    @Subscribe
    public void sendMessage(Messages.SendMessageRequest request){
        Messages.SendMessageResponse response = new Messages.SendMessageResponse();

        if (request.getMessage().equals("error")){
            response.setOperationError("Some error gappened");
        } else if (request.getMessage().equals("error-message")){
            response.setPropertyError("message", "Invalid message");
        }
        postDelayed(response, 1500, 3000);
    }

    @Subscribe
    public void markMessageAsRead(Messages.MarkMessageAsReadRequest request){
        postDelayed(new Messages.MarkMessageAsReadResponse());
    }

    @Subscribe
    public void getMessageDetails(Messages.GetMessageDetailRequest request){
        Messages.GetMessageDetailResponse response = new Messages.GetMessageDetailResponse();
        response.message = new Message(
                1,
                Calendar.getInstance(),
                "Short message",
                "Long message",
                null,
                new UserDetails(1, true, "DisplayName", "UserName", ""),
                false,
                false);
        postDelayed(response);
    }

}
