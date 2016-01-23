package com.spb.kbv.messageapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.spb.kbv.messageapp.R;
import com.spb.kbv.messageapp.activities.BaseActivity;
import com.spb.kbv.messageapp.services.Contacts;
import com.spb.kbv.messageapp.views.ContactRequestsAdapter;
import com.squareup.otto.Subscribe;

public class PendingContactRequestFragment extends BaseFragment {
    private View progressFrame;
    private ContactRequestsAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_contact_request, container, false);
        progressFrame = view.findViewById(R.id.fragment_pending_contact_request_progressFrame);
        adapter = new ContactRequestsAdapter((BaseActivity) getActivity());

        ListView listview = (ListView)view.findViewById(R.id.fragment_pending_contact_request_list);
        listview.setAdapter(adapter);

        bus.post(new Contacts.GetContactRequestRequest(true));

        return view;
    }

    @Subscribe
    public void onGetContactRequest(final Contacts.GetContactRequestResponse response){
        scheduler.invokeOnResume(Contacts.GetContactRequestResponse.class, new Runnable() {
            @Override
            public void run() {
                progressFrame.animate()
                        .alpha(0)
                        .setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                progressFrame.setVisibility(View.GONE);
                            }
                        })
                        .start();
                if (!response.didSucceed()){
                    response.showErrorToast(getActivity());
                    return;
                }
                adapter.clear();
                adapter.addAll(response.requests);
            }
        });

    }
}
