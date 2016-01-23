package com.spb.kbv.messageapp.services;

import android.util.Log;

import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.infrastructure.RetrofitCallbackPost;
import com.squareup.otto.Subscribe;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class LiveMessageService extends BaseLiveService {

    public LiveMessageService(MessageApplication application, WebService api) {
        super(application, api);
    }

    @Subscribe
    public void sendMessage(Messages.SendMessageRequest request){
        api.sendMessgaes(
                new TypedString(request.getMessage()),
                new TypedString(request.getRecipient().getUsername()),
                new TypedFile("image/jpeg", new File(request.getImagePath().getPath())),
                new RetrofitCallbackPost<>(Messages.SendMessageResponse.class, bus));
    }

    @Subscribe
    public void searchMessages(Messages.SearchMessagesRequest request) {
        if (request.fromContactId != null && !request.fromContactId.equals("")){
            api.searchMessages(
                    request.fromContactId,
                    request.includeSentMessages,
                    request.includeReceivedMessages,
                    new RetrofitCallbackPost<>(Messages.SearchMessageResponse.class, bus));
        } else {
            api.searchMessages(
                    request.includeSentMessages,
                    request.includeReceivedMessages,
                    new RetrofitCallbackPost<>(Messages.SearchMessageResponse.class, bus));
        }
    }

    @Subscribe
    public void deleteMessage(Messages.DeleteMessageRequest request){
        api.deleteMessage(request.messageId, new RetrofitCallbackPost<>(Messages.DeleteMessageResponse.class, bus));
    }

    @Subscribe
    public void markMessageAsRead(Messages.MarkMessageAsReadRequest request){
        api.markMessageAsRead(request.messageId, new RetrofitCallbackPost<>(Messages.MarkMessageAsReadResponse.class, bus));
    }

    @Subscribe
    public void getMessageDetails(Messages.GetMessageDetailRequest request){
        api.getMessageDetails(request.id, new RetrofitCallbackPost<>(Messages.GetMessageDetailResponse.class, bus));
    }
}
