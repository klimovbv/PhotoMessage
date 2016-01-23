package com.spb.kbv.messageapp.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;

import com.spb.kbv.messageapp.infrastructure.ActionScheduler;
import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.squareup.otto.Bus;

public class BaseDialogFragment extends DialogFragment{
    protected MessageApplication application;
    protected Bus bus;
    protected ActionScheduler scheduler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MessageApplication)getActivity().getApplication();
        scheduler = new ActionScheduler(application);
        bus = application.getBus();
        bus.register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduler.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        scheduler.onPause();
    }
}
