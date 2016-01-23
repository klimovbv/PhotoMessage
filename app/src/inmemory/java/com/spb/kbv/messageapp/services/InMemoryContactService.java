package com.spb.kbv.messageapp.services;

import android.util.Log;

import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.services.entities.ContactRequest;
import com.spb.kbv.messageapp.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class InMemoryContactService extends BaseInMemoryService{


    public InMemoryContactService(MessageApplication application) {
        super(application);
    }

    @Subscribe
    public void getContactRequest(Contacts.GetContactRequestRequest request){
        Contacts.GetContactRequestResponse response = new Contacts.GetContactRequestResponse();
        response.requests = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            response.requests.add(new ContactRequest(request.fromUs, createFakeUser(i, false), new GregorianCalendar()));
        }

        postDelayed(response);
    }

    @Subscribe
    public void getContacts(Contacts.GetContactRequest request){
        Contacts.GetContactResponse response = new Contacts.GetContactResponse();
        response.contacts = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            response.contacts.add(createFakeUser(i, true));
        }

        postDelayed(response);
    }

    @Subscribe
    public void sendContactsRequest(Contacts.SendContactRequestRequest request){
        if (request.userId == 2){
            Contacts.SendContactRequestResponse response = new Contacts.SendContactRequestResponse();
            response.setOperationError("Some error");
            postDelayed(response);
        }
        postDelayed(new Contacts.SendContactRequestResponse());
    }

    @Subscribe
    public void respondToContactsRequest(Contacts.RespondToContactRequestRequest reauest){
        postDelayed(new Contacts.RespondToContactRequestResponse());
    }

    @Subscribe
    public void removeContact(Contacts.RemoveContactRequest request){
        Contacts.RemoveContactResponse response = new Contacts.RemoveContactResponse();
        response.removedContactId = request.contactId;
        postDelayed(response);
    }

    @Subscribe
    public void searchUsers(Contacts.SearchUserRequest request){
        Contacts.SearchUserResponse response = new Contacts.SearchUserResponse();
        response.query = request.query;
        response.users = new ArrayList<>();
        for (int i = 0; i < request.query.length(); i++){
            response.users.add(createFakeUser(i, false));
        }


        postDelayed(response, 2000, 3000);

    }

    private UserDetails createFakeUser(int id, boolean isContact) {
        String idString = Integer.toString(id);
        return new UserDetails(
                id,
                isContact,
                "Contact " + idString,
                "Contact" + idString,
                "http://www.gravatar.com/avatar/" + idString + "?d=identicon&s=64");
    }
}
