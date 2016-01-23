package com.spb.kbv.messageapp.services;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.spb.kbv.messageapp.infrastructure.ServiceResponse;
import com.spb.kbv.messageapp.services.entities.Message;
import com.spb.kbv.messageapp.services.entities.UserDetails;

import java.util.List;

public final class Messages {
    private Messages(){
    }

    public static class DeleteMessageRequest {
        public String messageId;

        public DeleteMessageRequest(String messageId) {
            this.messageId = messageId;
        }
    }

    public static class DeleteMessageResponse extends ServiceResponse{
        public String messageId;
    }

    public static class SearchMessagesRequest {
        public String fromContactId;
        public boolean includeSentMessages;
        public boolean includeReceivedMessages;

        public SearchMessagesRequest(String fromContactId, boolean includeSentMessages, boolean includeReceivedMessages) {
            this.fromContactId = fromContactId;
            this.includeSentMessages = includeSentMessages;
            this.includeReceivedMessages = includeReceivedMessages;
        }

        public SearchMessagesRequest(boolean includeSentMessages, boolean includeReceivedMessages) {
            this.fromContactId = "";
            this.includeSentMessages = includeSentMessages;
            this.includeReceivedMessages = includeReceivedMessages;
        }
    }

    public static class SearchMessageResponse extends ServiceResponse {
        public List<Message> messages;
    }

    public static class SendMessageRequest implements Parcelable {
        private UserDetails recipient;
        private Uri imagePath;
        private String message;

        public SendMessageRequest (){}

        private SendMessageRequest (Parcel in ) {
            recipient = in.readParcelable(UserDetails.class.getClassLoader());
            imagePath = in.readParcelable(Uri.class.getClassLoader());
            message = in.readString();
        }
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int i) {
            out.writeParcelable(recipient, 0);
            out.writeParcelable(imagePath, 0);
            out.writeString(message);

        }


        public void setRecipient(UserDetails recipient) {
            this.recipient = recipient;
        }

        public void setImagePath(Uri imagePath) {
            this.imagePath = imagePath;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public UserDetails getRecipient() {
            return recipient;
        }

        public Uri getImagePath() {
            return imagePath;
        }

        public String getMessage() {
            return message;
        }

        public static Creator<SendMessageRequest> CREATOR = new Creator<SendMessageRequest>() {
            @Override
            public SendMessageRequest createFromParcel(Parcel parcel) {
                return new SendMessageRequest(parcel);
            }

            @Override
            public SendMessageRequest[] newArray(int size) {
                return new SendMessageRequest[size];
            }
        };
    }

    public static class SendMessageResponse extends ServiceResponse{
        public Message message;
    }

    public static class MarkMessageAsReadRequest{
        public String messageId;

        public MarkMessageAsReadRequest(String messageId) {
            this.messageId = messageId;
        }
    }

    public static class MarkMessageAsReadResponse extends ServiceResponse {

    }

    public static class GetMessageDetailRequest {
        public String id;

        public GetMessageDetailRequest(String id) {
            this.id = id;
        }
    }

    public static class GetMessageDetailResponse extends ServiceResponse {
        public Message message;
    }
}
