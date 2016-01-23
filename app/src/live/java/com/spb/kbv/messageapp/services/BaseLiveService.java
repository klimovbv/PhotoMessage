package com.spb.kbv.messageapp.services;

import com.spb.kbv.messageapp.infrastructure.MessageApplication;
import com.squareup.otto.Bus;

public abstract class BaseLiveService {
    protected final Bus bus;
    protected final WebService api;
    protected final MessageApplication application;

    protected BaseLiveService(MessageApplication application, WebService api) {
        this.bus = application.getBus();
        this.api = api;
        this.application = application;
        bus.register(this);
    }
}
