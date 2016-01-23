package com.spb.kbv.messageapp.services;

import com.google.gson.annotations.SerializedName;
import com.spb.kbv.messageapp.infrastructure.ServiceResponse;
import com.spb.kbv.messageapp.services.entities.ContactRequest;
import com.spb.kbv.messageapp.services.entities.UserDetails;

import java.util.List;

public final class Contacts {
    private Contacts () {}

    public static class GetContactRequestRequest {
        public boolean fromUs;

        public GetContactRequestRequest(boolean fromUs) {
            this.fromUs = fromUs;
        }
    }

    public static class GetContactRequestResponse extends ServiceResponse {
        public List<ContactRequest> requests;
    }

    public static class GetContactRequest {
        public boolean includePendingContacts;

        public GetContactRequest(boolean includePendingContacts) {
            this.includePendingContacts = includePendingContacts;
        }
    }

    public static class GetContactResponse extends ServiceResponse{
        public List<UserDetails> contacts;
    }

    public static class SendContactRequestRequest{
        public String username;

        public SendContactRequestRequest(String username) {
            this.username = username;
        }
    }

    public static class SendContactRequestResponse extends ServiceResponse{
    }

    public static class RespondToContactRequestRequest {
        public String username;
        public boolean accept;

        public RespondToContactRequestRequest(String username, boolean accept) {
            this.username = username;
            this.accept = accept;
        }
    }

    public static class RespondToContactRequestResponse extends ServiceResponse{
    }

    public static class RemoveContactRequest{
        public String username;

        public RemoveContactRequest(String username/*int contactId*/) {
            this.username = username;
        }
    }

    public static class RemoveContactResponse extends ServiceResponse {
        public String removedContactUsername;

    }

    public static class SearchUserRequest {
        public String query;

        public SearchUserRequest(String query) {
            this.query = query;
        }
    }

    public static class SearchUserResponse extends ServiceResponse {
        public List<UserDetails> users;
        @SerializedName("query")
        public String query;
    }
}
