package com.spb.kbv.messageapp.services;

import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.spb.kbv.messageapp.infrastructure.RetrofitCallbackPost;
import com.squareup.otto.Subscribe;

public class LiveContactService extends BaseLiveService {

    public LiveContactService(MessageApplication application, WebService api) {
        super(application, api);
    }

    @Subscribe
    public void searchUsers(Contacts.SearchUserRequest request){
        api.searchUsers(request.query, new RetrofitCallbackPost<Contacts.SearchUserResponse>(Contacts.SearchUserResponse.class, bus));
    }

    @Subscribe
    public void sendContactRequest(Contacts.SendContactRequestRequest request){
        api.sendContactRequest(request.username, new RetrofitCallbackPost<>(Contacts.SendContactRequestResponse.class, bus));
    }

    @Subscribe
    public void getContactRequest(Contacts.GetContactRequestRequest request){
        if (request.fromUs) {
            api.getContactRequestsFromUs(new RetrofitCallbackPost<>(Contacts.GetContactRequestResponse.class, bus));
        } else {
            api.getCOntactRequestsToUs(new RetrofitCallbackPost<>(Contacts.GetContactRequestResponse.class, bus));
        }
    }

    @Subscribe
    public void respondToContactRequest(Contacts.RespondToContactRequestRequest request){
        String response;
        if (request.accept) {
            response = "accept";
        } else {
            response = "reject";
        }

        api.respondToContactRequest(
                request.username,
                new WebService.RespondToContactRequest(response),
                new RetrofitCallbackPost<>(Contacts.RespondToContactRequestResponse.class, bus));
    }

    @Subscribe
    public void getContacts(Contacts.GetContactRequest request){
        api.getContacts(new RetrofitCallbackPost<>(Contacts.GetContactResponse.class, bus));
    }

    @Subscribe
    public void removeContacts(Contacts.RemoveContactRequest request){
        api.removeContact(request.username, new RetrofitCallbackPost<>(Contacts.RemoveContactResponse.class, bus));
    }

}
